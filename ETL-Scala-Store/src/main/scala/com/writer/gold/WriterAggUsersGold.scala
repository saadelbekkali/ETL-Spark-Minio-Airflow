package com.writer.gold

import com.utils.Utils.{PathAggUsersGold, PathInfoUsersGold, goldBucket}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types._


object WriterAggUsersGold extends WriterIcebergBase{

  override val basePath: String = PathAggUsersGold
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = goldBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "gold_catalog.aggUsers"
  val schema: StructType = StructType(Seq(
    StructField("city", StringType, nullable = true),
    StructField("numUsers", IntegerType, nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))

}
