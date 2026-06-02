package com.example.features.competences

import com.example.testApplicationWithDb
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertTrue

class CompetenceRoutesTest {
  @Test
  fun `mentor can successfully add and remove competences`() =
    testApplicationWithDb {
      val routeAdd = "/api/competences/add-to-expert"
      val routeRemove = "/api/competences/remove-from-expert"

      val expEmail = "comp_${UUID.randomUUID()}@test.com"
      client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(
          """{"email":"$expEmail","passwordHash":"123",""" +
            """"authProvider":"local","fullName":"Exp","role":"Mentor"}""",
        )
      }

      val token =
        client
          .post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$expEmail","passwordHash":"123"}""")
          }.bodyAsText()
          .let { Regex(""""token"\s*:\s*"([^"]+)"""").find(it)?.groupValues?.get(1) ?: "" }

      // Добавляем
      val addRes =
        client.post(routeAdd) {
          bearerAuth(token)
          contentType(ContentType.Application.Json)
          setBody("""{"tagName":"Kotlin Backend"}""")
        }

      assertTrue(
        addRes.status == HttpStatusCode.OK || addRes.status == HttpStatusCode.Created,
        "Ошибка добавления. Статус: ${addRes.status}. Убедись, что routeAdd правильный!",
      )

      // Удаляем (возможно, у тебя тут метод client.post вместо delete)
      val removeRes =
        client.delete(routeRemove) {
          bearerAuth(token)
          contentType(ContentType.Application.Json)
          setBody("""{"tagName":"Kotlin Backend"}""")
        }

      assertTrue(
        removeRes.status == HttpStatusCode.OK || removeRes.status == HttpStatusCode.NoContent,
        "Ошибка удаления. Статус: ${removeRes.status}.",
      )
    }
}
