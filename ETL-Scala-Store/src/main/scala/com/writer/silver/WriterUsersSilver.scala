package com.writer.silver

import com.utils.Utils.{PathProducts, PathUsers, silverBucket}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types._


object WriterUsersSilver extends WriterIcebergBase{

  override val basePath: String = PathUsers
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = silverBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "silver_catalog.users"
  val schema: StructType = StructType(Seq(
    StructField("id", IntegerType, nullable = true),
    StructField("fullName", StringType, nullable = true),
    StructField("streetAddress", StringType, nullable = true),
    StructField("numAddress", IntegerType, nullable = true),
    StructField("city", StringType, nullable = true),
    StructField("zipcodeAddress", StringType, nullable = true),
    StructField("phone", StringType, nullable = true),
    StructField("email", StringType, nullable = true),
    StructField("username", StringType, nullable = true),
    StructField("password", StringType, nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))



}
