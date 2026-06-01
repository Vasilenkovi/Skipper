package com.example

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ServerTest {
  @Test
  fun `test root endpoint returns 404 because no frontpage`() = testApplicationWithDb {
    val response = client.get("/")
    // Теперь мы знаем, что корня нет, и ждем 404
    assertEquals(HttpStatusCode.NotFound, response.status)
  }
}
