package config

import com.config.ApiConfig
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ApiConfigTest extends AnyFunSuite with Matchers {

  test("ApiConfig.loadConfig should correctly load the base URL from application.conf") {
    // Act: Load configuration from test resources
    val apiConfig = ApiConfig.loadConfig()

    // Assert: Ensure the value matches what is in your test `application.conf`
    apiConfig.baseUrl shouldBe "https://fakestoreapi.com" // Change this based on your actual test config
  }
}