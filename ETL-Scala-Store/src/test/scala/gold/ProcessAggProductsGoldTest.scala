package gold

import com.gold.AggProducts.ProcessAggProductsGold
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class ProcessAggProductsGoldTest extends AnyFunSuite with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("AggProductsGoldTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample input data as DataFrame
  val inputDf: DataFrame = Seq(
    (1L, "Laptop", "electronics", 999.99, 150.0, 210.0, 4.5),
    (2L, "Phone", "electronics", 499.99, 75.0, 105.0, 4.2),
    (3L, "Headphones", "accessories", 99.99, 15.0, 21.0, 3.8)
  ).toDF("id", "title", "category", "price", "Benefits", "tax", "rate")

  test("ProcessAggProductsGold should successfully aggregate product data") {
    // Act
    val resultTry: Try[DataFrame] = ProcessAggProductsGold.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get

    resultDf.columns should contain allOf("category", "numProducts", "totalBenefits", "totalTax", "avgBenefits", "avgPrice", "avgRate", "DateIngestion")
    resultDf.count() shouldBe 2 // Two categories: electronics and accessories

    val electronicsRow = resultDf.filter(col("category") === "electronics").collect()(0)
    electronicsRow.getAs[Long]("numProducts") shouldBe 2
    electronicsRow.getAs[Double]("totalBenefits") shouldBe (150.0 + 75.0 +- 0.01)
    electronicsRow.getAs[Double]("totalTax") shouldBe (210.0 + 105.0 +- 0.01)
    electronicsRow.getAs[Double]("avgBenefits") shouldBe ((150.0 + 75.0) / 2 +- 0.01)
    electronicsRow.getAs[Double]("avgPrice") shouldBe ((999.99 + 499.99) / 2 +- 0.01)
    electronicsRow.getAs[Double]("avgRate") shouldBe ((4.5 + 4.2) / 2 +- 0.01)
  }

  test("ProcessAggProductsGold should handle empty input data") {
    // Arrange
    val emptyDf = spark.createDataFrame(spark.sparkContext.emptyRDD[org.apache.spark.sql.Row], inputDf.schema)

    // Act
    val resultTry = ProcessAggProductsGold.ProcessDataframe(emptyDf)

    // Assert
    resultTry.isSuccess shouldBe true
    resultTry.get.count() shouldBe 0
  }
}

