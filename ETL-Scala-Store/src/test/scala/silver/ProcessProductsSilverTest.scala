package silver


import com.silver.products.ProcessProductsSilver
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

class ProcessProductsSilverTest extends AnyFlatSpec with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("ProductSilverTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample input data
  val sampleJson =
    """
    {
      "id": 1,
      "title": "Test Product",
      "price": 99.99,
      "rating": {
        "rate": 4.5,
        "count": 100
      },
      "category": "electronics"
    }
    """

  // Create schema for the input data
  val ratingSchema = StructType(Seq(
    StructField("rate", DoubleType, true),
    StructField("count", IntegerType, true)
  ))

  val inputSchema = StructType(Seq(
    StructField("id", LongType, false),
    StructField("title", StringType, true),
    StructField("price", DoubleType, true),
    StructField("rating", ratingSchema, true),
    StructField("category", StringType, true)
  ))

  // Helper function to create test DataFrame
  def createTestDataFrame(): DataFrame = {
    spark.read.schema(inputSchema).json(Seq(sampleJson).toDS())
  }

  "ProcessProductsSilver" should "successfully transform valid input data" in {
    // Arrange
    val inputDf = createTestDataFrame()

    // Act
    val resultTry = ProcessProductsSilver.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true

    val resultDf = resultTry.get
    resultDf.columns should contain allOf(
      "id", "title", "price", "countRating", "rate", "category",
      "DateIngestion", "Benefits", "price_range", "popularity", "tax"
    )

    // Test specific transformations
    val row = resultDf.collect()(0)
    row.getAs[Double]("Benefits") shouldBe (99.99 * 0.15 +- 0.01) // Using approximate equality for doubles
    row.getAs[String]("price_range") shouldBe "mid-range"
    row.getAs[String]("popularity") shouldBe "high"
    row.getAs[Double]("tax") shouldBe (99.99 * 0.21 +- 0.01)
  }

  it should "handle null values in input data" in {
    // Arrange
    val nullJson =
      """
      {
        "id": 2,
        "title": null,
        "price": null,
        "rating": {
          "rate": null,
          "count": null
        },
        "category": null
      }
      """
    val nullDf = spark.read.schema(inputSchema).json(Seq(nullJson).toDS())

    // Act
    val resultTry = ProcessProductsSilver.ProcessDataframe(nullDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get
    resultDf.filter(col("id") === 2).count() shouldBe 1
  }

  it should "correctly categorize products based on price" in {
    // Arrange
    val priceTestData = Seq(
      (1L, "Cheap Product", 49.99),
      (2L, "Mid Product", 99.99),
      (3L, "Expensive Product", 150.0)
    ).toDF("id", "title", "price")
      .withColumn("rating", struct(
        lit(4.0).as("rate"),
        lit(100).as("count")
      ))
      .withColumn("category", lit("test"))

    // Act
    val resultTry = ProcessProductsSilver.ProcessDataframe(priceTestData)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get

    val priceRanges = resultDf.select("id", "price_range").collect()
    priceRanges.find(_.getLong(0) == 1).get.getString(1) shouldBe "cheap"
    priceRanges.find(_.getLong(0) == 2).get.getString(1) shouldBe "mid-range"
    priceRanges.find(_.getLong(0) == 3).get.getString(1) shouldBe "expensive"
  }

  it should "correctly categorize popularity based on rating" in {
    // Arrange
    val ratingTestData = Seq(
      (1L, 2.9),
      (2L, 3.0),
      (3L, 4.5)
    ).toDF("id", "rating_rate")
      .withColumn("price", lit(99.99))
      .withColumn("title", lit("test"))
      .withColumn("rating", struct(
        col("rating_rate").as("rate"),
        lit(100).as("count")
      ))
      .withColumn("category", lit("test"))
      .drop("rating_rate")

    // Act
    val resultTry = ProcessProductsSilver.ProcessDataframe(ratingTestData)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get

    val popularities = resultDf.select("id", "popularity").collect()
    popularities.find(_.getLong(0) == 1).get.getString(1) shouldBe "low"
    popularities.find(_.getLong(0) == 2).get.getString(1) shouldBe "high"
    popularities.find(_.getLong(0) == 3).get.getString(1) shouldBe "high"
  }

  /*
  // Clean up after all tests
  override def afterAll(): Unit = {
    spark.stop()
  }

   */
}
