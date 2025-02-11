# Import required libraries from Airflow and Spark
from airflow import DAG
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from datetime import datetime, timedelta

# Define default arguments for the DAG
default_args = {
    'owner': 'saadelbekkali',  # Owner of the DAG
    'depends_on_past': False,  # Whether tasks depend on previous runs
    'start_date': datetime(2025, 2, 4),  # Start date for the DAG
    'email_on_failure': False,  # Disable email notifications on failure
    'email_on_retry': False,  # Disable email notifications on retry
    'retries': 1,  # Number of retries on failure
    'retry_delay': timedelta(minutes=5)  # Delay between retries
}

# Create a new DAG instance with a daily schedule
dag = DAG(
    'DAG_ETL_daily_jobs',  # Name of the DAG
    default_args=default_args,  # Default arguments defined above
    description="ETL daily from FakeStoreApi",  # Description of the DAG
    schedule_interval="0 9 * * *",  # Run every day at 9:00 AM
    catchup=False,  # Disable backfilling
    tags=["ETL"]  # Tags for categorization
)

# Common configuration for SparkSubmitOperator
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

# Spark configuration settings
common_conf = {
    'spark.jars.packages': common_packages,  # List of required Spark packages
    'spark.driver.extraClassPath': '/opt/airflow/jars/*',  # Driver classpath for Spark
    'spark.executor.extraClassPath': '/opt/airflow/jars/*',  # Executor classpath for Spark
    'spark.hadoop.fs.s3a.endpoint': 'http://minio:9000',  # MinIO endpoint for S3 storage
    'spark.hadoop.fs.s3a.access.key': 'minio',  # Access key for MinIO
    'spark.hadoop.fs.s3a.secret.key': 'minioAdmin',  # Secret key for MinIO
    'spark.hadoop.fs.s3a.path.style.access': 'true',  # Enable path style access for S3
    'spark.hadoop.fs.s3a.impl': 'org.apache.hadoop.fs.s3a.S3AFileSystem',  # Use the S3A filesystem
    'spark.hadoop.fs.s3a.aws.credentials.provider': 'org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider',  # Credentials provider for AWS
    'spark.driver.userClassPathFirst': 'true',  # Prioritize user classpath
    'spark.executor.userClassPathFirst': 'true',  # Prioritize user classpath
    'spark.sql.extensions': 'org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions',  # Iceberg extension for Spark SQL
    'spark.sql.catalog.silver_catalog': 'org.apache.iceberg.spark.SparkCatalog',  # Define Iceberg catalog for Silver data
    'spark.sql.catalog.silver_catalog.type': 'hadoop',  # Specify catalog type
    'spark.sql.catalog.silver_catalog.warehouse': 's3a://silver/',  # Path for Silver data in S3
    'spark.sql.catalog.gold_catalog': 'org.apache.iceberg.spark.SparkCatalog',  # Define Iceberg catalog for Gold data
    'spark.sql.catalog.gold_catalog.type': 'hadoop',  # Specify catalog type
    'spark.sql.catalog.gold_catalog.warehouse': 's3a://gold/'  # Path for Gold data in S3
}

# Task to execute the Bronze job (initial data processing stage)
bronze_job = SparkSubmitOperator(
    task_id='bronze_job',  # Task ID
    application='/opt/airflow/jars/scala-maven-project-1.0.jar',  # Path to the Spark application JAR
    java_class="com.Main",  # Main class for the Spark job
    application_args=["bronze"],  # Arguments passed to the application
    packages=common_packages,  # Spark packages to include
    conn_id='spark_default',  # Connection ID for Spark
    conf=common_conf,  # Spark configuration
    verbose=True,  # Enable verbose logging
    dag=dag  # Assign to the current DAG
)

# Task to execute the Silver job (second data processing stage)
silver_job = SparkSubmitOperator(
    task_id='silver_job',  # Task ID
    application='/opt/airflow/jars/scala-maven-project-1.0.jar',  # Path to the Spark application JAR
    java_class="com.Main",  # Main class for the Spark job
    application_args=["silver"],  # Arguments passed to the application
    packages=common_packages,  # Spark packages to include
    conn_id='spark_default',  # Connection ID for Spark
    conf=common_conf,  # Spark configuration
    verbose=True,  # Enable verbose logging
    dag=dag  # Assign to the current DAG
)

# Task to execute the Gold job (final data processing stage)
gold_job = SparkSubmitOperator(
    task_id='gold_job',  # Task ID
    application='/opt/airflow/jars/scala-maven-project-1.0.jar',  # Path to the Spark application JAR
    java_class="com.Main",  # Main class for the Spark job
    application_args=["gold"],  # Arguments passed to the application
    packages=common_packages,  # Spark packages to include
    conn_id='spark_default',  # Connection ID for Spark
    conf=common_conf,  # Spark configuration
    verbose=True,  # Enable verbose logging
    dag=dag  # Assign to the current DAG
)

# Define the task dependencies: Bronze -> Silver -> Gold
bronze_job >> silver_job >> gold_job
