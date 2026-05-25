@file:Suppress("WildcardImport")
import com.example.configureRouting
import com.example.configureSecurity
import com.example.configureSerialization
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiSecurityTest {

    @Test
    fun `test unauthorized access to protected route returns 401`() = testApplication {
        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        val response = client.get("/api/users/profile")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test invalid json body returns 400 Bad Request`() = testApplication {
        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        val response = client.post("/api/users/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"email": "test@mail.ru", "password": "123" """)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
