package com.example.features.reviews

import com.example.testApplicationWithDb
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReviewRoutesTest {
  @Test
  fun `mentee gets error when leaving review without completed slot`() =
    testApplicationWithDb {
      // 1. Создаем Ментора и получаем его профиль
      val expEmail = "exp_${UUID.randomUUID()}@test.com"
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """{"email":"$expEmail","passwordHash":"123",""" +
            """"authProvider":"local","fullName":"Mentor","role":"Mentor"}""",
        )
      }
      val expToken =
        client
          .post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$expEmail","passwordHash":"123"}""")
          }.bodyAsText()
          .let { Regex(""""token"\s*:\s*"([^"]+)"""").find(it)?.groupValues?.get(1) ?: "" }

      val profileJson = client.get("/api/users/profile") { bearerAuth(expToken) }.bodyAsText()
      val expertId = Regex(""""id"\s*:\s*"?([^",\s}]+)"?""").find(profileJson)?.groupValues?.get(1) ?: ""

      // 2. Создаем Менти
      val menEmail = "men_${UUID.randomUUID()}@test.com"
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """{"email":"$menEmail","passwordHash":"123",""" +
            """"authProvider":"local","fullName":"Mentee","role":"Mentee"}""",
        )
      }
      val menToken =
        client
          .post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$menEmail","passwordHash":"123"}""")
          }.bodyAsText()
          .let { Regex(""""token"\s*:\s*"([^"]+)"""").find(it)?.groupValues?.get(1) ?: "" }

      // 3. Менти пытается оставить отзыв без реального слота (подставляем expertId как slotId)
      val revRes =
        client.post("/api/reviews/$expertId") {
          bearerAuth(menToken)
          contentType(ContentType.Application.Json)
          setBody("""{"rating":5,"comment":"Отличный ментор!"}""")
        }

      // Проверяем, что наша бизнес-логика успешно заблокировала этот запрос!
      assertEquals(HttpStatusCode.BadRequest, revRes.status, "Ожидалась защита (400 Bad Request)")
      assertTrue(revRes.bodyAsText().contains("Слот не найден"), "Ожидалось сообщение 'Слот не найден'")
    }
}
