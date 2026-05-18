package com.example.features.slots

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.slotRoutes(slotService: SlotService) {
    route("/api/slots") {
        authenticate {
            post("/{id}/book") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"]

                    if (slotId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Отсутствует ID слота"))
                        return@post
                    }

                    val errorMessage = slotService.bookSlot(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Слот успешно забронирован!"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, mapOf("error" to errorMessage))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Неизвестная ошибка"))
                }
            }

            post {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<CreateSlotRequest>()

                    val errorMessage = slotService.createSlot(userId, request)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.Created, mapOf("message" to "Слот на 1 час успешно создан"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to errorMessage))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Неверный формат даты. Ожидался ISO 8601 (например: 2026-06-01T12:00:00)")
                    )
                }
            }


            get {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slots = slotService.getSlotsForExpert(userId)

                    call.respond(HttpStatusCode.OK, slots)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Ошибка получения списка слотов"))
                }
            }

            get("/mentor/{mentorId}") {
                try {
                    val mentorId = call.parameters["mentorId"]
                    if (mentorId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Отсутствует ID ментора"))
                        return@get
                    }

                    val availableSlots = slotService.getAvailableSlotsForMentor(mentorId)
                    call.respond(HttpStatusCode.OK, availableSlots)

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Ошибка получения расписания"))
                }
            }

            delete("/{id}") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"]

                    if (slotId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Отсутствует ID слота"))
                        return@delete
                    }

                    val success = slotService.deleteSlot(userId, slotId)

                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Слот успешно удален"))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Слот не найден, не принадлежит вам или уже заблокирован/оплачен (статус не FREE)")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Неверный формат ID слота"))
                }
            }
        }
    }
}
