package com.example.features.competences

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.competenceRoutes(competenceService: CompetenceService) {
    route("/api/competences") {
        post("/add-to-expert"){
            try {
                val request = call.receive<AddCompetenceRequest>()
                val success = competenceService.addCompetenceToExpert(request)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Навык успешно привязан к эксперту"))
                } else{
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
