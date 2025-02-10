package gold

import com.gold.ProductsInfo.ProcessProductsInfoGold
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class ProcessProductsInfoGoldTest extends AnyFunSuite with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("ProductsInfoGoldTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample input data as DataFrame
  val inputDf: DataFrame = Seq(
    (1L, "Laptop", "electronics", 999.99, "expensive", "high"),
    (2L, "Phone", "electronics", 499.99, "mid-range", "medium"),
    (3L, "Headphones", "accessories", 99.99, "cheap", "low")
  ).toDF("id", "title", "category", "price", "price_range", "popularity")

  test("ProcessProductsInfoGold should successfully process product data") {
    // Act
    val resultTry: Try[DataFrame] = ProcessProductsInfoGold.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get

    resultDf.columns should contain allOf("id", "title", "category", "price", "price_range", "popularity", "DateIngestion")
    resultDf.count() shouldBe 3
  }

  test("ProcessProductsInfoGold should handle empty input data") {
    // Arrange
    val emptyDf = spark.createDataFrame(spark.sparkContext.emptyRDD[org.apache.spark.sql.Row], inputDf.schema)

    // Act
    val resultTry = ProcessProductsInfoGold.ProcessDataframe(emptyDf)

    // Assert
    resultTry.isSuccess shouldBe true
    resultTry.get.count() shouldBe 0
  }
}
