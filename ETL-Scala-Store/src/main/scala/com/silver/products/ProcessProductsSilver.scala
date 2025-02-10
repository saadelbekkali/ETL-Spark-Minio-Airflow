package com.silver.products

import com.DataFrameBase
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, date_format, lit, to_date, when}
import com.utils.Utils.today
import com.writer.IcebergTableCreation

import scala.util.{Failure, Success, Try}

/**
 * Object responsible for processing raw product data into the Silver layer.
 * Implements transformations, enrichments, and categorization.
 */
object ProcessProductsSilver extends DataFrameBase {

  /**
   * Processes the input DataFrame by applying transformations and enriching the data.
   *
   * @param DataframeIn Input DataFrame containing raw product data.
   * @return Transformed DataFrame wrapped in a Try for error handling.
   */
  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {

    Try {

      println("Starting Product processing")

      // Assign input DataFrame to a variable for better readability
      val dfProductsRaw = DataframeIn

      // Apply transformations and enrich data
      val dfSilver = dfProductsRaw
        .select(
          col("id").as("id"), // Product ID
          col("title").as("title"), // Product title
          col("price").as("price"), // Product price
          col("rating.count").as("countRating"), // Number of ratings
          col("rating.rate").as("rate"), // Average rating
          col("category").as("category") // Product category
        )
        // Add an ingestion date column formatted as yyyy-MM-dd
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

        // Compute estimated benefits (15% of price)
        .withColumn("Benefits", col("price") * 0.15)

        // Categorize products into price ranges
        .withColumn("price_range",
          when(col("price") < 50, "cheap")
            .when((col("price") > 50) && (col("price") < 110), "mid-range")
            .otherwise("expensive")
        )

        // Determine product popularity based on rating
        .withColumn("popularity",
          when(col("rate") < 3.0, "low")
            .otherwise("high")
        )

        // Compute tax as 21% of price
        .withColumn("tax", col("price") * 0.21)

      dfSilver

    } match {
      case Success(df) => Success(df)
      case Failure(e) =>
        // Log the error message and stack trace
        println(s"Error processing the DataFrame: ${e.getMessage}")
        e.printStackTrace()
        // Throw a runtime exception to propagate the error
        throw new RuntimeException("DataFrame processing failed", e)
    }
  }
}
