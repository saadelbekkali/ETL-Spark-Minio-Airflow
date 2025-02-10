package com.writer

import org.apache.spark.sql.SparkSession

object IcebergTableCreation {

  def createIcebergTable(spark: SparkSession): Unit = {
    // Set catalog and warehouse configuration (using Iceberg directly without Hive)
    spark.conf.set("spark.sql.catalog.silver_catalog", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set("spark.sql.catalog.silver_catalog.type", "hadoop") // Use 'hadoop' for non-Hive catalog
    spark.conf.set("spark.sql.catalog.silver_catalog.warehouse", "s3a://silver/") // MinIO bucket location

    spark.sql(
      """
    CREATE TABLE IF NOT EXISTS silver_catalog.products (
    id INT,
    title STRING,
    price DECIMAL(10,2),
    countRating INT,
    rate DECIMAL(10,2),
    category STRING,
    Benefits DECIMAL(10,2),
    price_range STRING,
    popularity STRING,
    tax DECIMAL(10,2),
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://silver/products'  -- Updated path to match warehouse structure
""")


    // Make sure the catalog is set up correctly, and refer to it in the table creation
    spark.sql(
      """
    CREATE TABLE IF NOT EXISTS silver_catalog.users (
    id INT,
    fullName STRING,
    streetAddress STRING,
    numAddress INT,
    city STRING,
    zipcodeAddress STRING,
    phone STRING,
    email STRING,
    username STRING,
    password STRING,
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://silver/users'  -- Updated path to match warehouse structure
""")

  }

  def createGoldIcebergTable(spark: SparkSession): Unit = {

    // Set catalog and warehouse configuration (using Iceberg directly without Hive)
    spark.conf.set("spark.sql.catalog.gold_catalog", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set("spark.sql.catalog.gold_catalog.type", "hadoop") // Use 'hadoop' for non-Hive catalog
    spark.conf.set("spark.sql.catalog.gold_catalog.warehouse", "s3a://gold/") // MinIO bucket location


    spark.sql("""
    CREATE TABLE IF NOT EXISTS gold_catalog.aggUsers (
    city STRING,
    numUsers INT,
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://gold/aggUsers'  -- Updated path to match warehouse structure
""")

    spark.sql("""
    CREATE TABLE IF NOT EXISTS gold_catalog.infoUsers (
    id INT,
    fullName STRING,
    phone STRING,
    email STRING,
    username STRING,
    password STRING,
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://gold/infoUsers'  -- Updated path to match warehouse structure
""")


    spark.sql("""
    CREATE TABLE IF NOT EXISTS gold_catalog.aggProducts (
    category STRING,
    numProducts INT,
    totalBenefits DECIMAL(10,2),
    totalTax DECIMAL(10,2),
    avgBenefits DECIMAL(10,2),
    avgPrice DECIMAL(10,2),
    avgRate  DECIMAL(10,2),
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://gold/aggProducts'  -- Updated path to match warehouse structure
""")


    // Make sure the catalog is set up correctly, and refer to it in the table creation
    spark.sql("""
    CREATE TABLE IF NOT EXISTS gold_catalog.infoProducts (
    id INT,
    title STRING,
    category STRING,
    price DECIMAL(10,2),
    price_range STRING,
    popularity STRING,
    DateIngestion DATE
  )
  USING iceberg
  PARTITIONED BY (identity(DateIngestion))  -- Ensure partitioning is applied
  LOCATION 's3a://gold/infoProducts'  -- Updated path to match warehouse structure
""")




  }




}


