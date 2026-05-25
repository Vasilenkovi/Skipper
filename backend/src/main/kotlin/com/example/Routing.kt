@file:Suppress("WildcardImport")
package com.example

import com.example.core.DatabaseFactory
import com.example.core.ErrorResponse
import com.example.features.competences.CompetenceService
import com.example.features.competences.competenceRoutes
import com.example.features.reviews.ReviewService
import com.example.features.reviews.reviewRouting
import com.example.features.slots.SlotService
import com.example.features.slots.slotRoutes
import com.example.features.users.UserService
import com.example.features.users.userRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {

    DatabaseFactory.init()
    val userService = UserService()
    val competenceService = CompetenceService()
    val slotService = SlotService()
    val reviewService = ReviewService()

    // Устанавливаем глобальный перехватчик ошибок
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error = "Внутренняя ошибка сервера", code = 500)
            )
        }
    }

    routing {
        userRoutes(userService)
        competenceRoutes(competenceService)
        slotRoutes(slotService)
        reviewRouting(reviewService)

        staticFiles("/uploads", File("uploads"))
    }
}
