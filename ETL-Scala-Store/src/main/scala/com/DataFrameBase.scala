package com

import org.apache.spark.sql.DataFrame
import scala.util.Try

trait DataFrameBase {

  def ProcessDataframe(DataframeIn:DataFrame) :  Try[DataFrame]

}
