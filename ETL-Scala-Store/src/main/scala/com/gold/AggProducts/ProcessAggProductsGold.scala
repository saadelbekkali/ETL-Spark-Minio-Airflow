package com.gold.AggProducts

import com.DataFrameBase
import com.utils.Utils.today
import com.writer.IcebergTableCreation
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{avg, count, date_format, lit, sum, to_date}

import scala.util.{Failure, Success, Try}

object ProcessAggProductsGold extends DataFrameBase {

  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {


    Try {

      println("Starting Product processing")

      // Assign input DataFrame to a variable for readability
      val dfProductsSilver = DataframeIn

      // Perform aggregation on the Silver layer to generate the Gold dataset
      val dfAggProductsGold = dfProductsSilver
        .groupBy("category") // Group by product category
        .agg(
          count("id").alias("numProducts"), // Count the number of products per category
          sum("Benefits").alias("totalBenefits"), // Sum of benefits per category
          sum("tax").alias("totalTax"), // Sum of tax per category
          avg("Benefits").alias("avgBenefits"), // Average benefits per category
          avg("price").alias("avgPrice"), // Average price per category
          avg("rate").alias("avgRate") // Average rate per category
        )
        // Add an ingestion date column formatted as yyyy-MM-dd
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

      dfAggProductsGold
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
