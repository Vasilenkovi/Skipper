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
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun Route.consultationRoutes(service: ConsultationService) {
  route("/api/consultations") {
    authenticate {
      bookRoute(service)
      getRoutes(service)
      confirmRoute(service)
      cancelRoute(service)
      linkRoute(service)
    }
  }
}

private fun Route.bookRoute(service: ConsultationService) {
  post("/book") {
    try {
      val userId =
        call
          .principal<JWTPrincipal>()
          ?.payload
          ?.getClaim("userId")
          ?.asString()
      if (userId == null) {
        call.respond(
          HttpStatusCode.Unauthorized,
          ErrorResponse(error = "Не авторизован", code = 401),
        )
        return@post
      }

      val request = call.receive<BookConsultationRequest>()
      val errorMessage = service.bookConsultation(userId, request)

      if (errorMessage != null) {
        call.respond(
          HttpStatusCode.BadRequest,
          ErrorResponse(error = errorMessage, code = 400),
        )
      } else {
        call.respond(
          HttpStatusCode.Created,
          mapOf("message" to "Успешно забронировано"),
        )
      }
    } catch (_: Exception) {
      call.respond(
        HttpStatusCode.BadRequest,
        ErrorResponse(error = "Неверный формат", code = 400),
      )
    }
  }
}

private fun Route.getRoutes(service: ConsultationService) {
  get {
    val userId =
      call
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
    if (userId == null) {
      call.respond(
        HttpStatusCode.Unauthorized,
        ErrorResponse(error = "Не авторизован", code = 401),
      )
      return@get
    }
    call.respond(HttpStatusCode.OK, service.getConsultationsForUser(userId))
  }
}

private fun Route.confirmRoute(service: ConsultationService) {
  post("/{id}/confirm") {
    val expertId =
      call
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
    if (expertId == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return@post
    }

    val id = call.parameters["id"]?.toIntOrNull()
    if (id == null) {
      call.respond(HttpStatusCode.BadRequest)
      return@post
    }

    if (service.confirmConsultation(id, expertId)) {
      call.respond(HttpStatusCode.OK, mapOf("message" to "Консультация подтверждена"))
    } else {
      call.respond(
        HttpStatusCode.Forbidden,
        ErrorResponse(error = "Нет доступа", code = 403),
      )
    }
  }
}

private fun Route.cancelRoute(service: ConsultationService) {
  post("/{id}/cancel") {
    val userId =
      call
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
    if (userId == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return@post
    }

    val id = call.parameters["id"]?.toIntOrNull()
    if (id == null) {
      call.respond(HttpStatusCode.BadRequest)
      return@post
    }

    if (service.cancelConsultation(id, userId)) {
      call.respond(HttpStatusCode.OK, mapOf("message" to "Консультация отменена"))
    } else {
      call.respond(
        HttpStatusCode.Forbidden,
        ErrorResponse(error = "Нет доступа", code = 403),
      )
    }
  }
}

private fun Route.linkRoute(service: ConsultationService) {
  post("/{id}/link") {
    val expertId =
      call
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
    if (expertId == null) {
      call.respond(HttpStatusCode.Unauthorized)
      return@post
    }

    val id = call.parameters["id"]?.toIntOrNull()
    if (id == null) {
      call.respond(HttpStatusCode.BadRequest)
      return@post
    }

    val request = call.receive<AttachMeetingLinkRequest>()

    if (service.attachMeetingLink(id, expertId, request.meetingLink)) {
      call.respond(HttpStatusCode.OK, mapOf("message" to "Ссылка добавлена"))
    } else {
      call.respond(
        HttpStatusCode.Forbidden,
        ErrorResponse(error = "Нет доступа", code = 403),
      )
    }
  }
}
