package com.utils

import org.apache.spark.sql.SparkSession

object SparkUtils {
  def getSparkSession(appName: String = "Default Spark App"): SparkSession = {
    SparkSession.builder()
      .appName(appName)
      // MinIO configurations
      .config("spark.hadoop.fs.s3a.endpoint", "http://minio:9000")
      .config("spark.hadoop.fs.s3a.access.key", "minio")
      .config("spark.hadoop.fs.s3a.secret.key", "minioAdmin")
      .config("spark.hadoop.fs.s3a.path.style.access", "true")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      // Iceberg configurations for silver_catalog
      .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
      .config("spark.sql.catalog.silver_catalog", "org.apache.iceberg.spark.SparkCatalog")
      .config("spark.sql.catalog.silver_catalog.type", "hadoop")
      .config("spark.sql.catalog.silver_catalog.warehouse", "s3a://silver/")
      .config("spark.sql.catalog.silver_catalog.s3.endpoint", "http://minio:9000")
      .config("spark.sql.catalog.silver_catalog.s3.access-key-id", "minio")
      .config("spark.sql.catalog.silver_catalog.s3.secret-access-key", "minioAdmin")
      .config("spark.sql.catalog.silver_catalog.s3.path-style-access", "true")
      // Iceberg configurations for gold_catalog
      .config("spark.sql.catalog.gold_catalog", "org.apache.iceberg.spark.SparkCatalog")
      .config("spark.sql.catalog.gold_catalog.type", "hadoop")
      .config("spark.sql.catalog.gold_catalog.warehouse", "s3a://gold/")
      .config("spark.sql.catalog.gold_catalog.s3.endpoint", "http://minio:9000")
      .config("spark.sql.catalog.gold_catalog.s3.access-key-id", "minio")
      .config("spark.sql.catalog.gold_catalog.s3.secret-access-key", "minioAdmin")
      .config("spark.sql.catalog.gold_catalog.s3.path-style-access", "true")
      .getOrCreate()
  }
}
