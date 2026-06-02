@file:Suppress("WildcardImport")

package com.example

import com.example.core.ErrorResponse
import com.example.features.availability.AvailabilityService
import com.example.features.availability.availabilityRoutes
import com.example.features.competences.CompetenceService
import com.example.features.competences.competenceRoutes
import com.example.features.consultations.ConsultationService
import com.example.features.consultations.consultationRoutes
import com.example.features.reviews.ReviewService
import com.example.features.reviews.reviewRouting
import com.example.features.slots.SlotService
import com.example.features.slots.slotRoutes
import com.example.features.users.UserService
import com.example.features.users.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting() {
  val userService = UserService()
  val competenceService = CompetenceService()
  val slotService = SlotService()
  val reviewService = ReviewService()
  val consultationService = ConsultationService()
  val availabilityService = AvailabilityService()

  // Устанавливаем глобальный перехватчик ошибок
  install(StatusPages) {
    exception<Throwable> { call, cause ->
      cause.printStackTrace()
      call.respond(
        HttpStatusCode.InternalServerError,
        ErrorResponse(error = "Внутренняя ошибка сервера", code = 500),
      )
    }
  }

  routing {
    userRoutes(userService)
    competenceRoutes(competenceService)
    slotRoutes(slotService)
    reviewRouting(reviewService)
    consultationRoutes(consultationService)
    availabilityRoutes(availabilityService)

    staticFiles("/uploads", File("uploads"))
  }
}
