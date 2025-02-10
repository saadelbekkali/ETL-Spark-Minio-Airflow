package com.gold.UsersInfo

import com.DataFrameBase
import com.utils.Utils.today
import com.writer.IcebergTableCreation
import org.apache.spark.sql.functions.{date_format, lit, to_date}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

object ProcessUsersInfoGold extends DataFrameBase {

  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {


    Try {

      println("Starting User processing")

      // Assign input DataFrame to a variable for readability
      val dfUsersSilver = DataframeIn

      // Select relevant user information columns for the Gold layer
      val dfUsersInfoGold = dfUsersSilver
        .select(
          "id", // User ID
          "fullName", // Full name of the user
          "phone", // Phone number
          "email", // Email address
          "username", // Username
          "password" // Password (consider encryption for security)
        )
        // Add an ingestion date column formatted as yyyy-MM-dd
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

      dfUsersInfoGold
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
