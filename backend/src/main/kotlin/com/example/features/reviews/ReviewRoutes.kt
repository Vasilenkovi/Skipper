@file:Suppress("WildcardImport")
package com.example.features.reviews

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
fun Route.reviewRouting(reviewService: ReviewService) {
    route("/api/reviews") {
        authenticate {
            post("/{slotId}") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["slotId"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Не указан ID слота", code = 400)
                    )

                    val request = call.receive<CreateReviewRequest>()

                    val errorMessage = reviewService.leaveReview(userId, slotId, request)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Отзыв успешно сохранен! Рейтинг обновлен."))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }
        }
    }
}
