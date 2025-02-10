package com.jobs

import com.utils.Utils.today
import com.utils.HttpUtils
import com.writer.bronze.{WriterProductBronze, WriterUsersBronze}
import org.apache.spark.sql.SaveMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BronzeJob {

  def execute(): Unit = {
    try {
      // Start the Bronze layer ingestion process
      println("Starting data ingestion process (Bronze)...")

      // Fetch product data from the external API
      println("Fetching Products Info...")
      val JSONProducts = HttpUtils.getProducts()

      // Fetch user data from the external API
      println("Fetching Users Info...")
      val JSONUsers = HttpUtils.getUsers()

      // Write all data to MinIO as the last step
      println("Writing data to MinIO...")

      // Store product data in the Bronze layer
      WriterProductBronze.writeToMinIO(
        data = JSONProducts
      )

      // Store user data in the Bronze layer
      WriterUsersBronze.writeToMinIO(
        data = JSONUsers
      )

      // Indicate successful completion of the process
      println("Data ingestion completed successfully in Bronze.")

    } catch {
      case e: Exception =>
        // Log the error
        println(s"Error during ingestion process: ${e.getMessage}")
        e.printStackTrace()
        // Throw a new exception to propagate the failure
        throw new RuntimeException("Bronze data ingestion failed", e)
    }
  }
}
