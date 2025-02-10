package com.gold.ProductsInfo

import com.DataFrameBase
import com.utils.Utils.today
import com.writer.IcebergTableCreation
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{date_format, lit, to_date}

import scala.util.{Failure, Success, Try}

object ProcessProductsInfoGold extends DataFrameBase {

  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {


    Try {

      println("Starting Product processing")

      // Assign input DataFrame to a variable for readability
      val dfProductsSilver = DataframeIn

      // Select relevant product information columns for the Gold layer
      val dfProductsInfoGold = dfProductsSilver
        .select(
          "id", // Product ID
          "title", // Product title
          "category", // Product category
          "price", // Product price
          "price_range", // Price range classification
          "popularity" // Popularity metric
        )
        // Add an ingestion date column formatted as yyyy-MM-dd
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

      dfProductsInfoGold
    } match {
      case Success(df) => Success(df)
      case Failure(e) =>
        // Log the error
        println(s"Error processing the DataFrame: ${e.getMessage}")
        e.printStackTrace()
        // Propagate the error by throwing it
        throw new RuntimeException("DataFrame processing failed", e)
    }
  }
}
