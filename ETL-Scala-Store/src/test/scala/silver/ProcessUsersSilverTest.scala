package silver

import com.silver.users.ProcessUsersSilver
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class ProcessUsersSilverTest extends AnyFunSuite with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("UsersSilverTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample JSON input data
  val sampleJson =
    """
    {
      "id": 1,
      "name": { "firstname": "John", "lastname": "Doe" },
      "address": {
        "city": "New York",
        "number": 123,
        "street": "Main St",
        "zipcode": "10001"
      },
      "email": "john.doe@example.com",
      "password": "password123",
      "phone": "123-456-7890",
      "username": "johndoe"
    }
    """

  // Define schema for the input data
  val nameSchema = StructType(Seq(
    StructField("firstname", StringType, true),
    StructField("lastname", StringType, true)
  ))

  val addressSchema = StructType(Seq(
    StructField("city", StringType, true),
    StructField("number", IntegerType, true),
    StructField("street", StringType, true),
    StructField("zipcode", StringType, true)
  ))

  val inputSchema = StructType(Seq(
    StructField("id", LongType, false),
    StructField("name", nameSchema, true),
    StructField("address", addressSchema, true),
    StructField("email", StringType, true),
    StructField("password", StringType, true),
    StructField("phone", StringType, true),
    StructField("username", StringType, true)
  ))

  // Helper function to create test DataFrame
  def createTestDataFrame(): DataFrame = {
    spark.read.schema(inputSchema).json(Seq(sampleJson).toDS())
  }

  test("ProcessUsersSilver should successfully transform valid input data") {
    // Arrange
    val inputDf = createTestDataFrame()

    // Act
    val resultTry: Try[DataFrame] = ProcessUsersSilver.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true

    val resultDf = resultTry.get
    resultDf.columns should contain allOf(
      "id", "fullName", "city", "numAddress", "streetAddress",
      "zipcodeAddress", "email", "password", "phone", "username", "DateIngestion"
    )

    val row = resultDf.collect()(0)
    row.getAs[String]("fullName") shouldBe "John Doe"
    row.getAs[String]("city") shouldBe "New York"
    row.getAs[String]("zipcodeAddress") shouldBe "10001"
  }

  test("ProcessUsersSilver should handle null values in input data") {
    // Arrange
    val nullJson =
      """
      {
        "id": 2,
        "name": { "firstname": null, "lastname": null },
        "address": {
          "city": null,
          "number": null,
          "street": null,
          "zipcode": null
        },
        "email": null,
        "password": null,
        "phone": null,
        "username": null
      }
      """
    val nullDf = spark.read.schema(inputSchema).json(Seq(nullJson).toDS())

    // Act
    val resultTry = ProcessUsersSilver.ProcessDataframe(nullDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get
    resultDf.filter(col("id") === 2).count() shouldBe 1
  }
}

