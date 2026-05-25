@file:Suppress("WildcardImport")
package com.example.features.competences

import com.example.core.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress("LongMethod", "TooGenericExceptionCaught", "SwallowedException")
fun Route.competenceRoutes(competenceService: CompetenceService) {
    route("/api/competences") {
        get("/") {

        }

        authenticate {
            post("/add-to-expert") {
                try {
                    val expertId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<AddCompetenceRequest>()

                    if (request.tagName.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Название навыка не может быть пустым", code = 400)
                        )
                        return@post
                    }

                    val success = competenceService.addCompetenceToExpert(
                        userIdFromToken = expertId,
                        tagName = request.tagName.trim()
                    )

                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Навык успешно привязан к эксперту"))
                    } else {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse(error = "Не удалось добавить навык. Возможно, он уже привязан.", code = 409)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Неверный формат данных", code = 400))
                }
            }

            delete("/remove-from-expert") {
                try {
                    val expertId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<RemoveCompetenceRequest>()

                    if (request.tagName.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Название навыка не может быть пустым", code = 400)
                        )
                        return@delete
                    }

                    val success = competenceService.removeCompetenceFromExpert(
                        userIdFromToken = expertId,
                        tagName = request.tagName.trim()
                    )

                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Навык успешно удален из профиля"))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(error = "Навык или профиль не найден", code = 404)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Неверный формат данных", code = 400))
                }
            }
        }
    }
}
