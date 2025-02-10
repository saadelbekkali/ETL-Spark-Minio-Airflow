package gold

import com.gold.AggUsers.ProcessAggUsersGold
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.util.Try

class ProcessAggUsersGoldTest extends AnyFunSuite with Matchers {

  // Create Spark Session for testing
  val spark: SparkSession = SparkSession
    .builder()
    .appName("AggUsersGoldTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Sample input data as DataFrame
  val inputDf: DataFrame = Seq(
    (1L, "New York"),
    (2L, "Los Angeles"),
    (3L, "New York"),
    (4L, "Chicago")
  ).toDF("id", "city")

  test("ProcessAggUsersGold should successfully aggregate user count by city") {
    // Act
    val resultTry: Try[DataFrame] = ProcessAggUsersGold.ProcessDataframe(inputDf)

    // Assert
    resultTry.isSuccess shouldBe true
    val resultDf = resultTry.get

    resultDf.columns should contain allOf("city", "numUsers", "DateIngestion")
    resultDf.filter(col("city") === "New York").select("numUsers").as[Long].collect().head shouldBe 2L
    resultDf.filter(col("city") === "Los Angeles").select("numUsers").as[Long].collect().head shouldBe 1L
    resultDf.filter(col("city") === "Chicago").select("numUsers").as[Long].collect().head shouldBe 1L
  }

  test("ProcessAggUsersGold should handle empty input data") {
    // Arrange
    val emptyDf = spark.createDataFrame(spark.sparkContext.emptyRDD[org.apache.spark.sql.Row], inputDf.schema)

    // Act
    val resultTry = ProcessAggUsersGold.ProcessDataframe(emptyDf)

    // Assert
    resultTry.isSuccess shouldBe true
    resultTry.get.count() shouldBe 0
  }
}
