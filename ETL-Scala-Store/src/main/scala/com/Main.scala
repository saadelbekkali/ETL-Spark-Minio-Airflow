package com

import com.jobs.{BronzeJob, GoldJob, SilverJob}


object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Por favor, proporciona el nombre del Job a ejecutar.")
      sys.exit(1)
    }

    val jobName = args(0).toLowerCase

    jobName match {
      case "bronze" => BronzeJob.execute()
      case "silver" => SilverJob.execute()
      case "gold"   => GoldJob.execute()
      case _        =>
        println("We have the following jobs: bronze, silver, gold. There is no job called '" + jobName + "'.")
    }
  }
}
