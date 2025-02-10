package com.readers

import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Base trait for reading data from different layers (Bronze & Silver).
 * Defines reusable logic for data reading operations.
 */
trait ReaderBase {

  // Compute today's date in two formats: one for file naming, one for filtering
  protected val today: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
  protected val todayFormatted: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  // Base path to be specified by classes extending this trait
  val basePath: String

  /**
   * Reads data from the Bronze layer stored in MinIO.
   *
   * @param spark Implicit SparkSession
   * @return DataFrame containing Bronze data
   */
  def readBronzeMinIO(implicit spark: SparkSession): DataFrame = {
    // Construct the full S3 path using today's date
    val path: String = s"${basePath}_$today"
    val fullPath = s"s3a://bronze/$path"

    // Log the reading operation
    println(s"Reading data from: $fullPath")

    // Read JSON data from MinIO
    spark.read.json(fullPath)
  }

  /**
   * Reads data from the Silver layer stored as an Iceberg table.
   * Filters only the latest ingestion date.
   *
   * @param spark Implicit SparkSession
   * @return DataFrame containing Silver data
   */
  def readSilverMinIO(implicit spark: SparkSession): DataFrame = {
    spark.read
      .format("iceberg") // Specify Iceberg table format
      .load(basePath) // Load the data from the configured path
      .filter(col("DateIngestion") === lit(todayFormatted)) // Filter for today's ingestion data
  }
}
