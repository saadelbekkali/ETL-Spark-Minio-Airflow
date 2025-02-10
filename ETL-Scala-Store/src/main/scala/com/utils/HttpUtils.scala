package com.utils

import com.config.ApiConfig
import io.circe.Json
import io.circe.parser._

object HttpUtils {
  // Load API configuration once
  private var config = ApiConfig.loadConfig()

  /**
   * Generic private method to fetch data from the API.
   * This method cannot be accessed outside this object.
   * @param endpoint The API endpoint to fetch data from.
   * @return JSON response from the API.
   */
  private def fetchData(endpoint: String): Json = {
    try {
      println(s"Fetching data from: ${config.baseUrl}$endpoint") // Debugging information
      val response = requests.get(s"${config.baseUrl}$endpoint")

      // Validate the response status
      if (response.statusCode >= 200 && response.statusCode < 300) {
        parse(response.text()).getOrElse(Json.Null) // Parse JSON response
      } else {
        throw new RuntimeException(s"Error fetching data: ${response.statusCode} - ${response.text()}")
      }
    } catch {
      case e: Exception => throw new RuntimeException(s"API call to $endpoint failed", e)
    }
  }

  /**
   * Public method to fetch product data.
   * This calls the private `fetchData` method to ensure controlled access.
   * @return JSON response containing product details.
   */
  def getProducts(): Json = fetchData("/products")


  /**
   * Public method to fetch user data.
   * @return JSON response containing user details.
   */
  def getUsers(): Json = fetchData("/users")
}
