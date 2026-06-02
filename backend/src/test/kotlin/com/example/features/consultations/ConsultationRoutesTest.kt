package com.example.features.consultations

import com.auth0.jwt.JWT
import com.example.testApplicationWithDb
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsultationRoutesTest {
  @Test
  fun `should successfully book a consultation end to end via API`() =
    testApplicationWithDb {
      val expEmail = "exp_${UUID.randomUUID()}@test.com"
      val menEmail = "men_${UUID.randomUUID()}@test.com"
      val pass = "123"

      // 1. Регистрируем и логиним Эксперта
      val expRegRes =
        client.post("/api/users/register") {
          contentType(ContentType.Application.Json)
          setBody(
            """{"email":"$expEmail","passwordHash":"$pass","authProvider":"local",
          |"fullName":"Exp","role":"EXPERT"}
            """.trimMargin(),
          )
        }
      assertEquals(HttpStatusCode.Created, expRegRes.status, "Ошибка: ${expRegRes.bodyAsText()}")

      val expLoginRes =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody("""{"email":"$expEmail","passwordHash":"$pass"}""")
        }
      val expToken = expLoginRes.bodyAsText().substringAfter("\"token\":\"").substringBefore("\"")
      val expertId = JWT.decode(expToken).getClaim("userId").asString()

      // 2. Регистрируем и логиним Менти
      val menRegRes =
        client.post("/api/users/register") {
          contentType(ContentType.Application.Json)
          setBody(
            """{"email":"$menEmail","passwordHash":"$pass","authProvider":"local",
          |"fullName":"Men","role":"MENTEE"}
            """.trimMargin(),
          )
        }
      assertEquals(HttpStatusCode.Created, menRegRes.status, "Ошибка: ${menRegRes.bodyAsText()}")

      val menLoginRes =
        client.post("/api/users/login") {
          contentType(ContentType.Application.Json)
          setBody("""{"email":"$menEmail","passwordHash":"$pass"}""")
        }
      val menteeToken = menLoginRes.bodyAsText().substringAfter("\"token\":\"").substringBefore("\"")

      // 3. Эксперт создает окно доступности
      val startStr = "2026-06-20T10:00:00"
      val endStr = "2026-06-20T11:00:00"

      val availRes =
        client.post("/api/availability") {
          header(HttpHeaders.Authorization, "Bearer $expToken")
          contentType(ContentType.Application.Json)
          setBody("""{"startTime":"$startStr","endTime":"$endStr"}""")
        }
      assertTrue(availRes.status == HttpStatusCode.Created || availRes.status == HttpStatusCode.OK)

      // 4. Менти бронирует это окно
      val bookRes =
        client.post("/api/consultations/book") {
          header(HttpHeaders.Authorization, "Bearer $menteeToken")
          contentType(ContentType.Application.Json)
          setBody("""{"expertId":"$expertId","startTime":"$startStr","endTime":"$endStr"}""")
        }

      assertEquals(HttpStatusCode.Created, bookRes.status)
    }

  @Test
  fun `should fetch and update consultation statuses`() =
    testApplicationWithDb {
      val expEmail = "exp2_${UUID.randomUUID()}@test.com"
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

      // Проверяем GET /api/consultations
      val getRes =
        client.get("/api/consultations") {
          header(HttpHeaders.Authorization, "Bearer $token")
        }
      assertEquals(HttpStatusCode.OK, getRes.status)

      // Проверяем 400 Bad Request при неверном ID
      val badIdRes =
        client.post("/api/consultations/9999/cancel") {
          header(HttpHeaders.Authorization, "Bearer $token")
        }
      assertEquals(HttpStatusCode.Forbidden, badIdRes.status)
    }
}
