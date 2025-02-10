package com.config

import com.typesafe.config.ConfigFactory

// Case class to represent the API configuration
case class ApiConfig(baseUrl: String)

object ApiConfig {

  /**
   * Loads the API configuration from the application.conf file.
   * This method uses the Typesafe Config library to read configuration values.
   *
   * @return An instance of ApiConfig containing the base URL.
   */
  def loadConfig(): ApiConfig = {
    // Load the configuration file (application.conf is automatically detected)
    val config = ConfigFactory.load()

    // Extract the base URL from the configuration
    ApiConfig(
      baseUrl = config.getString("fakeStoreApi.baseUrl") // Reads "fakeStoreApi.baseUrl" from application.conf
    )
  }
}
