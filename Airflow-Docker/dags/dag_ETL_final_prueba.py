from airflow import DAG
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'saadelbekkali',
    'depends_on_past': False,
    'start_date': datetime(2025, 2, 4),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5)
}

dag = DAG(
    'DAG_ETL_daily_jobs_v2',
    default_args=default_args,
    schedule_interval="0 9 * * *",  # Ejecuta diariamente a las 9:00 AM
    catchup=False
)

# Configuración común para SparkSubmitOperator
common_packages = (
    "com.typesafe:config:1.4.2,"
    "com.lihaoyi:requests_2.12:0.8.0,"
    "io.circe:circe-core_2.12:0.14.1,"
    "io.circe:circe-parser_2.12:0.14.1,"
    "io.circe:circe-generic_2.12:0.14.1,"
    "org.apache.hadoop:hadoop-aws:3.3.1,"
    "com.amazonaws:aws-java-sdk-bundle:1.12.539,"
    "org.apache.iceberg:iceberg-spark-runtime-3.4_2.12:1.3.1"
)

common_conf = {
    'spark.jars.packages': common_packages,
    'spark.driver.extraClassPath': '/opt/airflow/jars/*',
    'spark.executor.extraClassPath': '/opt/airflow/jars/*',
    'spark.hadoop.fs.s3a.endpoint': 'http://minio:9000',
    'spark.hadoop.fs.s3a.access.key': 'minio',
    'spark.hadoop.fs.s3a.secret.key': 'minioAdmin',
    'spark.hadoop.fs.s3a.path.style.access': 'true',
    'spark.hadoop.fs.s3a.impl': 'org.apache.hadoop.fs.s3a.S3AFileSystem',
    'spark.hadoop.fs.s3a.aws.credentials.provider': 'org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider',
    'spark.driver.userClassPathFirst': 'true',
    'spark.executor.userClassPathFirst': 'true',
    'spark.sql.extensions': 'org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions',
    'spark.sql.catalog.silver_catalog': 'org.apache.iceberg.spark.SparkCatalog',
    'spark.sql.catalog.silver_catalog.type': 'hadoop',
    'spark.sql.catalog.silver_catalog.warehouse': 's3a://silver/',
    'spark.sql.catalog.gold_catalog': 'org.apache.iceberg.spark.SparkCatalog',
    'spark.sql.catalog.gold_catalog.type': 'hadoop',
    'spark.sql.catalog.gold_catalog.warehouse': 's3a://gold/'
}

# Task para ejecutar el job Bronze
bronze_job = SparkSubmitOperator(
    task_id='bronze_job',
    application='/opt/airflow/jars/scala-maven-project-2.0-SNAPSHOT.jar',
    java_class="com.Main",
    application_args=["bronze"],
    packages=common_packages,
    conn_id='spark_default',
    conf=common_conf,
    verbose=True,
    dag=dag
)

# Task para ejecutar el job Silver
silver_job = SparkSubmitOperator(
    task_id='silver_job',
    application='/opt/airflow/jars/scala-maven-project-2.0-SNAPSHOT.jar',
    java_class="com.Main",
    application_args=["silver"],
    packages=common_packages,
    conn_id='spark_default',
    conf=common_conf,
    verbose=True,
    dag=dag
)

# Task para ejecutar el job Gold
gold_job = SparkSubmitOperator(
    task_id='gold_job',
    application='/opt/airflow/jars/scala-maven-project-2.0-SNAPSHOT.jar',
    java_class="com.Main",
    application_args=["gold"],
    packages=common_packages,
    conn_id='spark_default',
    conf=common_conf,
    verbose=True,
    dag=dag
)



bronze_job >> silver_job >> gold_job
