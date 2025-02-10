package com.writer.gold

import com.utils.Utils.{PathInfoProductsGold, goldBucket}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types.{DateType, DecimalType, IntegerType, StringType, StructField, StructType}

object WriterInfoProductsGold extends WriterIcebergBase{

  override val basePath: String = PathInfoProductsGold
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = goldBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "gold_catalog.infoProducts"
  val schema: StructType = StructType(Seq(
    StructField("id", IntegerType, nullable = true),
    StructField("title", StringType, nullable = true),
    StructField("category", StringType, nullable = true),
    StructField("price", DecimalType(10,2), nullable = true),
    StructField("price_range", StringType, nullable = true),
    StructField("popularity", StringType, nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))




}
