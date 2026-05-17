package com.example.features.competences

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                            status = HttpStatusCode.BadRequest,
                            message = mapOf("error" to "Название навыка не может быть пустым")
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
                            mapOf("error" to "Не удалось добавить навык. Возможно, он уже привязан.")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Неверный формат данных"))
                }
            }

        }
    }
}
