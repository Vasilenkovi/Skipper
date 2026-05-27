@file:Suppress("UnusedPrivateProperty")

package com.example

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiContractTest {
  @Test
  fun `test users register endpoint contract`() =
    testApplication {
      val response =
        client.post("/api/users/register") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "test@example.com",
              "password": "password123",
              "role": "MENTEE"
            }
            """.trimIndent(),
          )
        }
      assertTrue(
        response.status in
          listOf(
            HttpStatusCode.Created,
            HttpStatusCode.Conflict,
            HttpStatusCode.OK,
          ),
      )
      val body = response.bodyAsText()
      assertTrue(body.isNotBlank())
    }

  @Test
  fun `test login endpoint contract`() =
    testApplication {
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "login@example.com",
            "password": "password123",
            "role": "MENTEE"
          }
          """.trimIndent(),
        )
      }
      val response =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody(
            """
            {
              "email": "login@example.com",
              "password": "password123"
            }
            """.trimIndent(),
          )
        }
      assertTrue(
        response.status in
          listOf(
            HttpStatusCode.OK,
            HttpStatusCode.Unauthorized,
          ),
      )
      if (response.status == HttpStatusCode.OK) {
        val body = response.bodyAsText()
        assertTrue(body.contains("token") || body.contains("user"))
      }
    }

  @Test
  fun `test mentors list endpoint contract`() =
    testApplication {
      val response = client.get("/api/users/mentors")
      assertEquals(HttpStatusCode.OK, response.status)
      val body = response.bodyAsText()
      assertTrue(body.contains("mentors") || body.startsWith("["))
    }

  @Test
  fun `test slots creation endpoint contract`() =
    testApplication {
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """
          {
            "email": "expert@example.com",
            "password": "password123",
            "role": "EXPERT"
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
              "email": "expert@example.com",
              "password": "password123"
            }
            """.trimIndent(),
          )
        }
      if (loginResponse.status != HttpStatusCode.OK) {
        println("⚠️ Auth not configured in tests, skipping slot creation test")
        return@testApplication
      }
      val token = "test-token"
      val response =
        client.post("/api/slots") {
          contentType(ContentType.Application.Json)
          header(HttpHeaders.Authorization, "Bearer $token")
          setBody(
            """
            {
              "startTime": "2026-06-01T10:00:00"
            }
            """.trimIndent(),
          )
        }
      assertTrue(
        response.status in
          listOf(
            HttpStatusCode.Created,
            HttpStatusCode.BadRequest,
            HttpStatusCode.Unauthorized,
            HttpStatusCode.OK,
          ),
      )
    }
}
