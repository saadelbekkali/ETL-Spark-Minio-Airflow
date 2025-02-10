package com.jobs

import com.readers.bronze.{ProductReader, UsersReader}
import com.silver.products.ProcessProductsSilver
import com.silver.users.ProcessUsersSilver
import com.utils.SparkUtils
import com.writer.IcebergTableCreation
import com.writer.silver.{WriterProductSilver, WriterUsersSilver}
import org.apache.spark.sql.SparkSession

object SilverJob {

  def execute(): Unit = {
    try {
      // Start the Silver layer processing
      println("Starting data ingestion process (Silver)...")

      // Initialize Spark session
      val sparkSilver: SparkSession = SparkUtils.getSparkSession("Silver Process")

      // Create the Iceberg table if it does not exist
      IcebergTableCreation.createIcebergTable(sparkSilver)

      // ------------------------------ READ BRONZE DATA ------------------------------\\

      println("Reading Product Data from Bronze layer")
      val dfProductBronze = ProductReader.readBronzeMinIO(sparkSilver)

      println("Reading User Data from Bronze layer")
      val dfUsersBronze = UsersReader.readBronzeMinIO(sparkSilver)

      // ------------------------------ PROCESS PRODUCTS ------------------------------\\

      println("Processing Product Data for Silver layer")
      val dfProductProcessed = ProcessProductsSilver.ProcessDataframe(dfProductBronze).get

      println("Writing Processed Product Data to Silver layer")
      WriterProductSilver.writeDataFrameToMinIO(dfProductProcessed)

      // ------------------------------ PROCESS USERS ------------------------------\\

      println("Processing User Data for Silver layer")
      val dfUsersProcessed = ProcessUsersSilver.ProcessDataframe(dfUsersBronze).get

      println("Writing Processed User Data to Silver layer")
      WriterUsersSilver.writeDataFrameToMinIO(dfUsersProcessed)

      println("Silver layer processing completed successfully.")

    } catch {
      case e: Exception =>
        // Log the error message and stack trace
        println(s"Error during Silver layer ingestion process: ${e.getMessage}")
        e.printStackTrace()
        // Throw an exception to propagate the failure
        throw new RuntimeException("Silver data ingestion failed", e)
    }
  }
}
