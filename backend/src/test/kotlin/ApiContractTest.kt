@file:Suppress("WildcardImport")

package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test users register endpoint contract`() = testApplication {
        val response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "email": "test@example.com",
                    "password": "password123",
                    "role": "MENTEE"
                }
            """.trimIndent())
        }

        // Проверяем, что ответ соответствует контракту
        assertTrue(
            response.status in listOf(HttpStatusCode.Created, HttpStatusCode.Conflict)
        )
        
        val body = response.bodyAsText()
        assertTrue(body.contains("user") || body.contains("error"))
    }

    @Test
    fun `test login endpoint contract`() = testApplication {
        val response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
            """.trimIndent())
        }

        // Проверяем структуру ответа
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("token"))
        assertTrue(body.contains("userId"))
        assertTrue(body.contains("role"))
    }

    @Test
    fun `test mentors list endpoint contract`() = testApplication {
        val response = client.get("/api/users/mentors")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("mentors"))
        assertTrue(body.contains("total"))
    }

    @Test
    fun `test slots creation endpoint contract`() = testApplication {
        // Сначала регистрируемся и логинимся
        val registerResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "email": "expert@example.com",
                    "password": "password123",
                    "role": "EXPERT"
                }
            """.trimIndent())
        }

        val loginResponse = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "email": "expert@example.com",
                    "password": "password123"
                }
            """.trimIndent())
        }

        val token = json.decodeFromString<LoginResponse>(loginResponse.bodyAsText()).token

        // Создаем слот
        val response = client.post("/api/slots") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("""
                {
                    "startTime": "2026-06-01T10:00:00"
                }
            """.trimIndent())
        }

        assertTrue(
            response.status in listOf(HttpStatusCode.Created, HttpStatusCode.BadRequest)
        )
    }
}

@kotlinx.serialization.Serializable
data class LoginResponse(
    val token: String,
    val userId: String,
    val role: String
)
