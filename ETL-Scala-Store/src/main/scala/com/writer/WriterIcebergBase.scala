package com.writer


import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SaveMode}

trait WriterIcebergBase {

  val basePath: String
  val schema: StructType
  val partitionColumns: Seq[String]
  val pathCatalog: String

  val bucket: String
  val mode: SaveMode


  def writeDataFrameToMinIO(df: DataFrame): Unit = {
    try {
       println(s"Starting to write $pathCatalog ")

      // Ensure the DataFrame matches the predefined schema
      val validatedDF = df.select(schema.map(f => df.col(f.name).cast(f.dataType)): _*)

      spark.conf.set("spark.sql.iceberg.handle-timestamp-without-timezone", true)
      spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")

      // Construct the full path
      val fullPath = s"s3a://$bucket/$basePath"

      // Basic writer configuration
       validatedDF.write
         .format("iceberg")
         .mode(mode)
         .option("overwrite-mode", "dynamic")
         .partitionBy(partitionColumns: _*)
         .save(pathCatalog)


      // Write the DataFrame

      println(s"Data written successfully to Iceberg table in MinIO: $fullPath")
    } catch {
      case e: Exception =>
        println(s"Error writing Iceberg table to MinIO: ${e.getMessage}")
        throw e
    }
  }
}