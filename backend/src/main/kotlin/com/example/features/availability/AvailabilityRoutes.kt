package com.example.features.availability

import com.example.core.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun Route.availabilityRoutes(availabilityService: AvailabilityService) {
  route("/api/availability") {
    authenticate {
      post {
        val userId =
          call
            .principal<JWTPrincipal>()!!
            .payload
            .getClaim("userId")
            .asString()
        val request =
          try {
            call.receive<CreateAvailabilityRequest>()
          } catch (e: Exception) {
            return@post call.respond(
              HttpStatusCode.BadRequest,
              ErrorResponse(error = "Неверный формат тела запроса", code = 400),
            )
          }

        val errorMessage = availabilityService.createWindow(userId, request)

        if (errorMessage == null) {
          call.respond(HttpStatusCode.Created, mapOf("message" to "Окно доступности успешно создано"))
        } else {
          call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
        }
      }
    }
  }
}
