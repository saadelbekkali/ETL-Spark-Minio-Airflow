package com.silver.users

import com.DataFrameBase
import com.utils.Utils.today
import com.writer.IcebergTableCreation
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

object ProcessUsersSilver extends DataFrameBase {

  // Override the ProcessDataframe method to process input DataFrame and return a Try[DataFrame]
  override def ProcessDataframe(DataframeIn: DataFrame): Try[DataFrame] = {

    Try {
      // Starting message for the process
      println("Starting Product processing")

      // The input DataFrame (raw user data)
      val dfUsersRaw = DataframeIn

      // Processing the raw DataFrame to create the Silver DataFrame
      val dfUsersSilver = dfUsersRaw
        // Create a new column 'fullName' by concatenating 'firstname' and 'lastname'
        .withColumn("fullName", concat(col("name.firstname"), lit(" "), col("name.lastname")))
        // Select relevant columns and rename some for clarity
        .select(
          col("id").as("id"),  //User Id
          col("fullName"),     // Full name User
          col("address.city").as("city"),  //User city
          col("address.number").as("numAddress"),  //User numAddress
          col("address.street").as("streetAddress"),  //User street Address
          col("address.zipcode").as("zipcodeAddress"),  //User zipcode Address
          col("email"), //User email
          col("password"), //User password
          col("phone"), //User phone
          col("username") //User username
        )
        // Add a 'DateIngestion' column with today's date
        .withColumn("DateIngestion", date_format(to_date(lit(today), "yyyyMMdd"), "yyyy-MM-dd"))

      // Return the processed DataFrame
      dfUsersSilver
    } match {
      // If successful, return the processed DataFrame
      case Success(df) => Success(df)

      // If there is a failure, log the error and propagate the exception
      case Failure(e) =>
        println(s"Error processing the DataFrame: ${e.getMessage}")
        e.printStackTrace()
        // Throw a RuntimeException to propagate the error
        throw new RuntimeException("DataFrame processing failed", e)
    }
  }
}
