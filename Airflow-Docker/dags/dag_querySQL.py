from datetime import datetime
from airflow import DAG
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator

default_args = {
    'owner': 'saadelbekkali',
    'start_date': datetime(2025, 1, 1),
    'retries': 1
}

dag = DAG(
    'sparkSQL',
    default_args=default_args,
    description = "Dag to show SQL queries to the iceberg catalog",
    schedule_interval=None,
    catchup=False,
    tags = ["SQL"]
)

# Define the Spark submit operator with correct configurations for Bitnami setup
spark_job = SparkSubmitOperator(
    task_id='spark_minio_task',
    application='/opt/airflow/dags/query_SQLSpark_Airflow.py',
    conn_id='spark_default',
    packages= "org.apache.hadoop:hadoop-aws:3.3.1,"
                                  "com.amazonaws:aws-java-sdk-bundle:1.12.539,"
                                  "org.apache.iceberg:iceberg-spark-runtime-3.4_2.12:1.3.1",
    conf={
        'spark.jars.packages':    "org.apache.hadoop:hadoop-aws:3.3.1,"
                                  "com.amazonaws:aws-java-sdk-bundle:1.12.539,"
                                  "org.apache.iceberg:iceberg-spark-runtime-3.4_2.12:1.3.1",
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
        'spark.sql.catalog.gold_catalog': 'org.apache.iceberg.spark.SparkCatalog',  # Define Iceberg catalog for Gold data
        'spark.sql.catalog.gold_catalog.type': 'hadoop',  # Specify catalog type
        'spark.sql.catalog.gold_catalog.warehouse': 's3a://gold/'  # Path for Gold data in S3
    },
    verbose=True,
    dag=dag
)