package com.example.features.users

import com.example.testApplicationWithDb
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
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
}
