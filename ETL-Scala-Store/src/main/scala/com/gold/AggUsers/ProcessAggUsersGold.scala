package com.gold.AggUsers

import com.DataFrameBase
import com.utils.Utils.today
import com.writer.IcebergTableCreation
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{count, date_format, lit, to_date}

import scala.util.{Failure, Success, Try}

object ProcessAggUsersGold  extends DataFrameBase {

  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {


    Try {

      println("Starting User processing")

      // Assign input DataFrame to a variable for readability
      val dfUsersSilver = DataframeIn

      // Perform aggregation on the Silver layer to generate the Gold dataset
      val dfUsersAggGold = dfUsersSilver
        .groupBy("city") // Group by user city
        .agg(
          count("id").alias("numUsers") // Count the number of users per city
        )
        // Add an ingestion date column formatted as yyyy-MM-dd
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

      dfUsersAggGold
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
