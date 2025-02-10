package com.writer.silver

import com.utils.Utils.{PathProducts, silverBucket}
import com.writer.WriterIcebergBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types._


object WriterProductSilver extends WriterIcebergBase{

  override val basePath: String = PathProducts
  override val partitionColumns: Seq[String] = Seq("DateIngestion")
  override val bucket: String = silverBucket
  override val mode: SaveMode = SaveMode.Overwrite
  override val pathCatalog: String = "silver_catalog.products"
  val schema: StructType = StructType(Seq(
    StructField("id", IntegerType, nullable = true),
    StructField("title", StringType, nullable = true),
    StructField("price", DecimalType(10,2), nullable = true),
    StructField("countRating", IntegerType, nullable = true),
    StructField("rate", DecimalType(10,2), nullable = true),
    StructField("category", StringType, nullable = true),
    StructField("Benefits", DecimalType(10,2), nullable = true),
    StructField("price_range", StringType, nullable = true),
    StructField("popularity", StringType, nullable = true),
    StructField("tax", DecimalType(10,2), nullable = true),
    StructField("DateIngestion", DateType, nullable = false) // Formato "yyyy-MM-dd"

  ))



}
