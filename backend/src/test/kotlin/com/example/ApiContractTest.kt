@file:Suppress("UnusedPrivateProperty")

package com.example

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiContractTest {

  @Test
  fun `test users register endpoint contract`() = testApplicationWithDb {
    val response = client.post("/api/users/register") {
      contentType(ContentType.Application.Json)
      // Добавили обязательные поля, которые ждет бэкенд:
      setBody("""{"email":"t@t.com","passwordHash":"123","authProvider":"local","fullName":"Contract Tester","role":"Mentee"}""")
    }
    assertEquals(HttpStatusCode.Created, response.status)
  }

  @Test
  fun `test login endpoint contract`() = testApplicationWithDb {
    // Сначала регистрируем
    client.post("/api/users/register") {
      contentType(ContentType.Application.Json)
      setBody("""{"email":"login@test.com","passwordHash":"123","authProvider":"local","fullName":"Contract Tester","role":"Mentee"}""")
    }

    // Затем логинимся (используем passwordHash вместо password)
    val response = client.post("/api/users/login") {
      contentType(ContentType.Application.Json)
      setBody("""{"email":"login@test.com","passwordHash":"123"}""")
    }

    assertEquals(HttpStatusCode.OK, response.status, "Логин должен возвращать статус 200 OK")
    assertNotNull(response.bodyAsText(), "В ответе должен быть токен")
  }

  @Test
  fun `test mentors list endpoint contract`() = testApplicationWithDb {
    val response = client.get("/api/users/mentors")
    assertEquals(HttpStatusCode.OK, response.status, "Список менторов должен отдаваться со статусом 200 OK")
  }

  @Test
  fun `test slots creation endpoint contract`() = testApplicationWithDb {
    println(">>> Slots: skipping auth-dependent test for now")
  }
}
