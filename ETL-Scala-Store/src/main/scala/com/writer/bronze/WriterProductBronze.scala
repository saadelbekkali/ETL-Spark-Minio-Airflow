package com.writer.bronze

import com.utils.Utils.{PathProducts, bronzeBucket}
import com.writer.WriterJsonBase
import org.apache.spark.sql.SaveMode

object WriterProductBronze extends WriterJsonBase {
  override val basePath: String = PathProducts
  override val bucket: String = bronzeBucket
  override val mode: SaveMode = SaveMode.Overwrite
}