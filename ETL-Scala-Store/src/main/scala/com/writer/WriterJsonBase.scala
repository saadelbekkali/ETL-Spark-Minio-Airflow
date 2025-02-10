package com.writer

import com.utils.SparkUtils
import com.utils.Utils.today
import io.circe.Json
import org.apache.spark.sql.{SaveMode, SparkSession}

trait WriterJsonBase {
  protected val spark: SparkSession =  SparkUtils.getSparkSession("Write Json in Bronze")

  val basePath: String
  val bucket: String
  val format: String = "json"
  val mode: SaveMode


  def writeToMinIO(data: Json): Unit = {
    try {
      println("new method:")
      val path: String = s"${basePath}_$today"
      println(s"Starting writeToMinIO for path: $path")
      println(s"Using bucket: $bucket")

      val jsonString = data.noSpaces
      println(s"JSON string length: ${jsonString.length}")

      import spark.implicits._

      println("Converting to Dataset...")
      val jsonDS = Seq(jsonString).toDS()

      println("Reading as DataFrame...")
      val jsonDF = spark.read.json(jsonDS)



      val fullPath = s"s3a://$bucket/$path"
      println(s"Writing to full path: $fullPath")

      jsonDF.write
        .mode(mode)
        .format(format)
        .save(fullPath)

      println(s"Data written successfully to MinIO: $fullPath")
    } catch {
      case e: Exception =>
        val errorMsg = Option(e.getMessage).getOrElse("No error message")
        val cause = Option(e.getCause).map(_.getMessage).getOrElse("No cause")
        println(s"Error writing data to MinIO: $errorMsg")
        println(s"Cause: $cause")
        e.printStackTrace()
        throw e
    }
  }
}