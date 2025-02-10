package gold

import com.gold.UsersInfo.ProcessUsersInfoGold
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class ProcessUsersInfoGoldTest extends AnyFunSuite with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("UsersInfoGoldTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample input data as DataFrame
  val inputDf: DataFrame = Seq(
    (1L, "John Doe", "123-456-7890", "john@example.com", "johndoe", "password123"),
    (2L, "Jane Smith", "987-654-3210", "jane@example.com", "janesmith", "securepass"),
    (3L, "Alice Brown", "555-666-7777", "alice@example.com", "aliceb", "mypassword")
  ).toDF("id", "fullName", "phone", "email", "username", "password")

  test("ProcessUsersInfoGold should successfully process user data") {
    // Act
    val resultTry: Try[DataFrame] = ProcessUsersInfoGold.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get


    resultDf.columns should contain allOf("id", "fullName", "phone", "email", "username", "password", "DateIngestion")
    resultDf.count() shouldBe 3
  }

  test("ProcessUsersInfoGold should handle empty input data") {
    // Arrange
    val emptyDf = spark.createDataFrame(spark.sparkContext.emptyRDD[org.apache.spark.sql.Row], inputDf.schema)

    // Act
    val resultTry = ProcessUsersInfoGold.ProcessDataframe(emptyDf)

    // Assert
    resultTry.isSuccess shouldBe true
    resultTry.get.count() shouldBe 0
  }
}