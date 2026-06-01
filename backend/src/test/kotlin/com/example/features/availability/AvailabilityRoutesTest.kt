package com.example.features.availability

import com.example.core.JwtConfig
import com.example.features.users.Users
import com.example.testApplicationWithDb
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

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
}
