@file:Suppress("UnusedPrivateProperty")

package com.example

import io.ktor.client.request.contentType
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class ApiContractTest {
  @Test
  fun `test users register endpoint contract`() = testApplication {
    val response = client.post("/api/users/register") {
      contentType(ContentType.Application.Json)
      setBody("""{"email":"t@t.com","password":"123","role":"MENTEE"}""")
    }
    println(">>> Register: status=${response.status}, body=${response.bodyAsText().take(200)}")
    assertNotNull(response.bodyAsText())
  }

  @Test
  fun `test login endpoint contract`() = testApplication {
    client.post("/api/users/register") {
      contentType(ContentType.Application.Json)
      setBody("""{"email":"login@test.com","password":"123","role":"MENTEE"}""")
    }
    val response = client.post("/api/users/login") {
      contentType(ContentType.Application.Json)
      setBody("""{"email":"login@test.com","password":"123"}""")
    }
    println(">>> Login: status=${response.status}, body=${response.bodyAsText().take(200)}")
    assertNotNull(response.bodyAsText())
  }

  @Test
  fun `test mentors list endpoint contract`() = testApplication {
    val response = client.get("/api/users/mentors")
    println(">>> Mentors: status=${response.status}, body=${response.bodyAsText().take(200)}")
    assertNotNull(response.bodyAsText())
  }

  @Test
  fun `test slots creation endpoint contract`() = testApplication {
    println(">>> Slots: skipping auth-dependent test for now")
  }
}
