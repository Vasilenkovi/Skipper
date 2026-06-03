package com.example.features.availability

import com.example.core.JwtConfig
import com.example.features.users.Users
import com.example.testApplicationWithDb
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AvailabilityRoutesTest {
  @Test
  fun `should create availability window successfully`() =
    testApplicationWithDb {
      val testUserId =
        transaction {
          Users
            .insertAndGetId {
              it[email] = "expert@test.com"
              it[passwordHash] = "hash"
              it[isEmailConfirmed] = true
              it[authProvider] = "email"
              it[fullName] = "Иван Иванов"
              it[role] = "Mentor"
            }.value
            .toString()
        }

      val testToken = JwtConfig.generateToken(testUserId, "Mentor")

      val requestBody =
        """
        {
            "startTime": "2027-06-01T12:00:00",
            "endTime": "2027-06-01T13:00:00"
        }
        """.trimIndent()

      // Act
      val response =
        client.post("/api/availability") {
          bearerAuth(testToken)
          contentType(ContentType.Application.Json)
          setBody(requestBody)
        }

      assertEquals(HttpStatusCode.Created, response.status)

      transaction {
        val windows = AvailabilityWindows.selectAll().toList()
        assertEquals(1, windows.size)
        assertEquals(testUserId, windows.first()[AvailabilityWindows.expertId])
      }
    }

  @Test
  fun `should return 400 when start time is in the past`() =
    testApplicationWithDb {
      val dummyUserId = UUID.randomUUID().toString()
      val testToken = JwtConfig.generateToken(dummyUserId, "Mentor")

      val requestBody =
        """
        {
            "startTime": "2020-01-01T12:00:00",
            "endTime": "2020-01-01T13:00:00"
        }
        """.trimIndent()

      // Act
      val response =
        client.post("/api/availability") {
          bearerAuth(testToken)
          contentType(ContentType.Application.Json)
          setBody(requestBody)
        }

      // Assert
      assertEquals(HttpStatusCode.BadRequest, response.status)

      transaction {
        val windows = AvailabilityWindows.selectAll().toList()
        assertEquals(0, windows.size)
      }
    }

  @Test
  fun `expert can successfully create an availability window`() =
    testApplicationWithDb {
      val expEmail = "avail_${UUID.randomUUID()}@test.com"
      val pass = "123"

      val regJson =
        """{"email":"$expEmail","passwordHash":"$pass",""" +
          """"authProvider":"local","fullName":"Exp","role":"EXPERT"}"""

      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(regJson)
      }

      val loginRes =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody("""{"email":"$expEmail","passwordHash":"$pass"}""")
        }
      val token = loginRes.bodyAsText().substringAfter("\"token\":\"").substringBefore("\"")

      val availRes =
        client.post("/api/availability") {
          bearerAuth(token) // <-- Вот оно, элегантное решение Ktor! Никаких HttpHeaders.
          contentType(ContentType.Application.Json)
          setBody("""{"startTime":"2026-08-01T10:00:00","endTime":"2026-08-01T11:00:00"}""")
        }

      assertTrue(
        availRes.status == HttpStatusCode.Created || availRes.status == HttpStatusCode.OK,
        "Ожидался успешный статус создания окна",
      )
    }
}
