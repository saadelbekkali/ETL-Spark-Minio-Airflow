package com.writer.gold

import com.utils.Utils.{PathInfoUsersGold, PathUsers, goldBucket, silverBucket, today}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.{date_format, lit, to_date}
import org.apache.spark.sql.types._


object WriterInfoUsersGold extends WriterIcebergBase{

  override val basePath: String = PathInfoUsersGold
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = goldBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "gold_catalog.infoUsers"
  val schema: StructType = StructType(Seq(
    StructField("id", IntegerType, nullable = true),
    StructField("fullName", StringType, nullable = true),
    StructField("phone", StringType, nullable = true),
    StructField("email", StringType, nullable = true),
    StructField("username", StringType, nullable = true),
    StructField("password", StringType, nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))



}
