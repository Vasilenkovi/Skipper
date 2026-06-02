package com.example.features.users

import com.example.testApplicationWithDb
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRoutesTest {
  @Test
  fun `test user registration returns 201 Created`() =
    testApplicationWithDb {
      val response =
        client.post("/api/users/register") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "test.unit@example.com",
              "passwordHash": "securepassword123",
              "authProvider": "local",
              "fullName": "Тестовый Пользователь",
              "role": "Mentee"
            }
            """.trimIndent(),
          )
        }

      assertEquals(HttpStatusCode.Created, response.status)
    }

  @Test
  fun `test duplicate email registration returns 409 Conflict`() =
    testApplicationWithDb {
      val requestJson =
        """
        {
          "email": "clone@example.com",
          "passwordHash": "securepassword123",
          "authProvider": "local",
          "fullName": "Иван Клон",
          "role": "Mentee"
        }
        """.trimIndent()

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(requestJson)
      }

      val secondResponse =
        client.post("/api/users/register") {
          contentType(ContentType.Application.Json)
          setBody(requestJson)
        }

      assertEquals(HttpStatusCode.Conflict, secondResponse.status)
    }

  @Test
  fun `test successful login returns 200 OK and token`() =
    testApplicationWithDb {
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "login.success@example.com",
            "passwordHash": "my_secret_password",
            "authProvider": "local",
            "fullName": "Успешный Логин",
            "role": "Mentee"
          }
          """.trimIndent(),
        )
      }

      val loginResponse =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "login.success@example.com",
              "passwordHash": "my_secret_password"
            }
            """.trimIndent(),
          )
        }

      assertEquals(HttpStatusCode.OK, loginResponse.status)

      val responseBody = loginResponse.bodyAsText()
      assert(responseBody.contains("token")) { "Ответ не содержит токена!" }
    }

  @Test
  fun `test login with wrong password returns 401 Unauthorized`() =
    testApplicationWithDb {
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "wrong.pass@example.com",
            "passwordHash": "correct_password",
            "authProvider": "local",
            "fullName": "Ошибочный Логин",
            "role": "Mentee"
          }
          """.trimIndent(),
        )
      }

      val loginResponse =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "wrong.pass@example.com",
              "passwordHash": "wrong_password_123!"
            }
            """.trimIndent(),
          )
        }

      assertEquals(HttpStatusCode.Unauthorized, loginResponse.status)
    }

  @Test
  fun `test email confirmation returns 200 OK with valid code`() =
    testApplicationWithDb {
      val testEmail = "confirm.success@example.com"

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "$testEmail",
            "passwordHash": "securepassword123",
            "authProvider": "local",
            "fullName": "Пользователь Подтверждение",
            "role": "Mentee"
          }
          """.trimIndent(),
        )
      }

      var confirmationCode = ""
      transaction {
        confirmationCode = Users
          .select { Users.email eq testEmail }
          .single()[Users.confirmationCode] ?: ""
      }

      val confirmResponse =
        client.post("/api/users/confirm") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "$testEmail",
              "code": "$confirmationCode"
            }
            """.trimIndent(),
          )
        }

      assertEquals(HttpStatusCode.OK, confirmResponse.status)
      assert(confirmResponse.bodyAsText().contains("token")) { "Ответ не содержит токена!" }
    }

  @Test
  fun `test email confirmation returns 400 Bad Request with invalid code`() =
    testApplicationWithDb {
      val testEmail = "confirm.fail@example.com"

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "$testEmail",
            "passwordHash": "securepassword123",
            "authProvider": "local",
            "fullName": "Неудачное Подтверждение",
            "role": "Mentee"
          }
          """.trimIndent(),
        )
      }

      val confirmResponse =
        client.post("/api/users/confirm") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "$testEmail",
              "code": "000000"
            }
            """.trimIndent(),
          )
        }

      assertEquals(HttpStatusCode.BadRequest, confirmResponse.status)
    }

  @Test
  fun `test access protected profile without token returns 401 Unauthorized`() =
    testApplicationWithDb {
      // Пытаемся получить профиль "с улицы", без токена
      val response = client.get("/api/users/profile")

      // Сервер должен нас прогнать
      assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

  @Test
  fun `test get profile with valid token returns 200 OK`() =
    testApplicationWithDb {
      val email = "get.profile@example.com"

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "$email",
            "passwordHash": "securepassword",
            "authProvider": "local",
            "fullName": "Иван Профилев",
            "role": "Expert"
          }
          """.trimIndent(),
        )
      }

      var code = ""
      transaction { code = Users.select { Users.email eq email }.single()[Users.confirmationCode] ?: "" }

      val confirmResponse =
        client.post("/api/users/confirm") {
          contentType(ContentType.Application.Json)
          setBody("""{"email": "$email", "code": "$code"}""")
        }

      val tokenRegex = Regex("\"token\"\\s*:\\s*\"([^\"]+)\"")
      val token =
        tokenRegex.find(confirmResponse.bodyAsText())?.groupValues?.get(1)
          ?: throw AssertionError("Токен не найден! Ответ сервера: ${confirmResponse.bodyAsText()}")

      val profileResponse =
        client.get("/api/users/profile") {
          header(HttpHeaders.Authorization, "Bearer $token")
        }

      assertEquals(
        HttpStatusCode.OK,
        profileResponse.status,
        "Ожидался статус 200 OK при запросе профиля, но сервер вернул: ${profileResponse.status}. " +
          "Тело ответа: ${profileResponse.bodyAsText()}",
      )
      assert(profileResponse.bodyAsText().contains("Иван Профилев"))
    }

  @Test
  fun `test update profile with valid token changes data and returns 200 OK`() =
    testApplicationWithDb {
      val email = "update.profile@example.com"

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "$email",
            "passwordHash": "pass",
            "authProvider": "local",
            "fullName": "Старое Имя",
            "role": "Expert"
          }
          """.trimIndent(),
        )
      }

      var code = ""
      transaction { code = Users.select { Users.email eq email }.single()[Users.confirmationCode] ?: "" }

      val confirmResp =
        client.post("/api/users/confirm") {
          contentType(ContentType.Application.Json)
          setBody("""{"email": "$email", "code": "$code"}""")
        }

      val tokenRegex = Regex("\"token\"\\s*:\\s*\"([^\"]+)\"")
      val token =
        tokenRegex.find(confirmResp.bodyAsText())?.groupValues?.get(1)
          ?: throw AssertionError("Токен не найден! Ответ: ${confirmResp.bodyAsText()}")

      val updateResponse =
        client.put("/api/users/update-profile") {
          header(HttpHeaders.Authorization, "Bearer $token")
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "newFullName": "Новое Имя",
              "contactInfo": "@new_telegram",
              "newExperienceDescription": "Обновленная биография"
            }
            """.trimIndent(),
          )
        }

      assertEquals(
        HttpStatusCode.OK,
        updateResponse.status,
        "Ожидался статус 200 OK при обновлении, но сервер вернул: ${updateResponse.status}.",
      )
      val verifyProfileResponse =
        client.get("/api/users/profile") {
          header(HttpHeaders.Authorization, "Bearer $token")
        }

      val verifyBody = verifyProfileResponse.bodyAsText()
      assert(verifyBody.contains("Новое Имя")) { "Профиль не обновил имя! Тело: $verifyBody" }
      assert(verifyBody.contains("@new_telegram")) { "Профиль не обновил контакт! Тело: $verifyBody" }
    }
}
