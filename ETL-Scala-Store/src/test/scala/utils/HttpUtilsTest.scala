package utils

import com.config.ApiConfig
import com.utils.HttpUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import io.circe.Json

class HttpUtilsTest extends AnyFunSuite with BeforeAndAfter {

  // Test para obtener productos
  test("getProducts should return valid JSON when API call is successful") {
    try {
      val json = HttpUtils.getProducts()

      // Verificar que obtuvimos un JSON válido
      assert(json.isArray) // Los productos vienen en formato de array

      // Imprimir el JSON para debug
      println(s"Received JSON: $json")

      // Verificar que cada producto tiene un id y un nombre
      json.asArray.foreach { products =>
        assert(products.nonEmpty, "No products found")
        products.foreach { product =>
          assert(product.hcursor.downField("id").as[Int].isRight)
          assert(product.hcursor.downField("title").as[String].isRight)
        }
      }
    } catch {
      case e: Exception =>
        println(s"Test failed with error: ${e.getMessage}")
        throw e
    }
  }

  // Test para obtener usuarios
  test("getUsers should return valid JSON when API call is successful") {
    try {
      val json = HttpUtils.getUsers()

      // Verificar que obtuvimos un JSON válido
      assert(json.isArray) // Los usuarios vienen en formato de array

      // Imprimir el JSON para debug
      println(s"Received JSON: $json")

      // Verificar que cada usuario tiene un id y un nombre (firstname y lastname)
      json.asArray.foreach { users =>
        assert(users.nonEmpty, "No users found")
        users.foreach { user =>
          println(s"User: $user") // Imprimir cada usuario para ver la estructura
          assert(user.hcursor.downField("id").as[Int].isRight)

          // Acceder a 'name' como objeto y verificar 'firstname' y 'lastname'
          val nameCursor = user.hcursor.downField("name")
          assert(nameCursor.succeeded, "Name field is missing or incorrect")

          val firstname = nameCursor.downField("firstname").as[String]
          val lastname = nameCursor.downField("lastname").as[String]

          // Verificar que 'firstname' y 'lastname' son strings válidos
          assert(firstname.isRight, "Firstname is missing or incorrect")
          assert(lastname.isRight, "Lastname is missing or incorrect")

          // Verificar que los campos no están vacíos
          firstname.foreach(name => assert(name.nonEmpty, "Firstname is empty"))
          lastname.foreach(name => assert(name.nonEmpty, "Lastname is empty"))
        }
      }
    } catch {
      case e: Exception =>
        println(s"Test failed with error: ${e.getMessage}")
        throw e
    }
  }




}