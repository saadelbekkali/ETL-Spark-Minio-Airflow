package com.jobs

import com.gold.AggProducts.ProcessAggProductsGold
import com.gold.AggUsers.ProcessAggUsersGold
import com.gold.ProductsInfo.ProcessProductsInfoGold
import com.gold.UsersInfo.ProcessUsersInfoGold
import com.readers.silver.{ProductsReaderSilver, UsersReaderSilver}
import com.utils.SparkUtils
import com.writer.IcebergTableCreation
import com.writer.gold.{WriterAggProductsGold, WriterAggUsersGold, WriterInfoProductsGold, WriterInfoUsersGold}
import org.apache.spark.sql.SparkSession

object GoldJob {

  def execute(): Unit = {
    try {
      // Start the Gold layer ingestion process
      println("Starting data ingestion process (Gold)...")

      // Initialize Spark session
      val sparkGold: SparkSession = SparkUtils.getSparkSession("Gold Process")

      // Create the Gold Iceberg table if it does not exist
      IcebergTableCreation.createGoldIcebergTable(sparkGold)

      // ------------------------------ READ SILVER DATA ------------------------------\\

      println("Reading dfProductSilver from Silver layer")
      val dfProductSilver = ProductsReaderSilver.readSilverMinIO(sparkGold)

      println("Reading dfUsersSilver from Silver layer")
      val dfUsersSilver = UsersReaderSilver.readSilverMinIO(sparkGold)

      // ------------------------------ PROCESS PRODUCTS ------------------------------\\

      println("Processing Product Information for Gold layer")
      val dfProductsInfoProcessed = ProcessProductsInfoGold.ProcessDataframe(dfProductSilver).get

      println("Writing Processed Product Information to Gold layer")
      WriterInfoProductsGold.writeDataFrameToMinIO(dfProductsInfoProcessed)

      println("Processing Aggregated Product Data for Gold layer")
      val dfAggProductProcessed = ProcessAggProductsGold.ProcessDataframe(dfProductSilver).get

      println("Writing Aggregated Product Data to Gold layer")
      WriterAggProductsGold.writeDataFrameToMinIO(dfAggProductProcessed)

      // ------------------------------ PROCESS USERS ------------------------------\\

      println("Processing User Information for Gold layer")
      val dfUsersInfoProcessed = ProcessUsersInfoGold.ProcessDataframe(dfUsersSilver).get

      println("Writing Processed User Information to Gold layer")
      WriterInfoUsersGold.writeDataFrameToMinIO(dfUsersInfoProcessed)

      println("Processing Aggregated User Data for Gold layer")
      val dfAggUsersProcessed = ProcessAggUsersGold.ProcessDataframe(dfUsersSilver).get

      println("Writing Aggregated User Data to Gold layer")
      WriterAggUsersGold.writeDataFrameToMinIO(dfAggUsersProcessed)

      println("Gold layer processing completed successfully.")

    } catch {
      case e: Exception =>
        // Log the error message and stack trace
        println(s"Error during Gold layer ingestion process: ${e.getMessage}")
        e.printStackTrace()
        // Throw an exception to propagate the failure
        throw new RuntimeException("Gold data ingestion failed", e)
    }
  }
}
