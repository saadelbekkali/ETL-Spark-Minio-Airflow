package com.utils

import org.apache.spark.sql.SparkSession

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {

  val today: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

  //Paths

  final val PathProducts = "Products/Products"
  final val PathUsers = "Users/Users"

  final val PathProductsSilver = "silver_catalog.products"
  final val PathUsersSilver = "silver_catalog.users"

  final val PathInfoProductsGold = "gold_catalog.infoProducts"
  final val PathAggProductsGold =  "gold_catalog.aggProducts"
  final val PathInfoUsersGold = "gold_catalog.infoUsers"
  final val PathAggUsersGold = "gold_catalog.aggUsers"

  // Buckets

  final val bronzeBucket = "bronze"
  final val silverBucket = "silver"
  final val goldBucket = "gold"


}
