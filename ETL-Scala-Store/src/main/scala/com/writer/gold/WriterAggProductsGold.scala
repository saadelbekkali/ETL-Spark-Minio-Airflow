package com.writer.gold

import com.utils.Utils.{PathAggProductsGold, goldBucket}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types._


object WriterAggProductsGold extends WriterIcebergBase{

  override val basePath: String = PathAggProductsGold
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = goldBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "gold_catalog.aggProducts"
  val schema: StructType = StructType(Seq(
    StructField("category", StringType, nullable = true),
    StructField("numProducts", IntegerType, nullable = true),
    StructField("totalBenefits", DecimalType(10,2), nullable = true),
    StructField("totalTax", DecimalType(10,2), nullable = true),
    StructField("avgBenefits", DecimalType(10,2), nullable = true),
    StructField("avgPrice", DecimalType(10,2), nullable = true),
    StructField("avgRate", DecimalType(10,2), nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))



}
