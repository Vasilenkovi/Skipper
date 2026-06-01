package com.example.features.consultations

import com.example.core.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun Route.consultationRoutes(consultationService: ConsultationService) {
  route("/api/consultations") {
    // Защищаем эндпоинт авторизацией (бронировать могут только залогиненные пользователи)
    authenticate {
      post("/book") {
        try {
          val principal = call.principal<JWTPrincipal>()
          val menteeId = principal?.payload?.getClaim("userId")?.asString()

          if (menteeId == null) {
            call.respond(
              HttpStatusCode.Unauthorized,
              ErrorResponse(error = "Не авторизован", code = 401),
            )
            return@post
          }

          val request = call.receive<BookConsultationRequest>()
          val errorMessage = consultationService.bookConsultation(menteeId, request)

          if (errorMessage != null) {
            call.respond(
              HttpStatusCode.BadRequest,
              ErrorResponse(error = errorMessage, code = 400),
            )
          } else {
            // 201 Created
            call.respond(
              HttpStatusCode.Created,
              mapOf("message" to "Консультация успешно забронирована и ожидает подтверждения экспертом"),
            )
          }
        } catch (_: Exception) {
          call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(error = "Неверный формат тела запроса", code = 400),
          )
        }
      }
    }
  }
}
