package com.writer.bronze

import com.utils.Utils.{PathUsers, bronzeBucket}
import com.writer.WriterJsonBase
import org.apache.spark.sql.SaveMode

object WriterUsersBronze extends WriterJsonBase{

  override val basePath: String = PathUsers
  override val bucket: String = bronzeBucket
  override val mode: SaveMode = SaveMode.Overwrite


}

