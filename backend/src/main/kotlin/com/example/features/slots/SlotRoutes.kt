
package com.example.features.slots

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

@Suppress("LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught", "SwallowedException")
fun Route.slotRoutes(slotService: SlotService) {
    route("/api/slots") {
        authenticate {
            post("/{id}/book") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"]

                    if (slotId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Отсутствует ID слота", code = 400)
                        )
                        return@post
                    }

                    val errorMessage = slotService.bookSlot(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Слот успешно забронирован!"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, ErrorResponse(error = errorMessage, code = 409))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Неизвестная ошибка", code = 400))
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
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            error = "Неверный формат даты. Ожидался ISO 8601 (например: 2026-06-01T12:00:00)",
                            code = 400
                        )
                    )
                }
            }

            post("/{id}/confirm-payment") {
                try {
                    val slotId = call.parameters["id"]
                    if (slotId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Отсутствует ID слота", code = 400)
                        )
                        return@post
                    }

                    val errorMessage = slotService.confirmPayment(slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Оплата прошла успешно! Слот забронирован."))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }

            post("/{id}/accept") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Отсутствует ID слота", code = 400)
                    )

                    val errorMessage = slotService.acceptSlotRequest(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Заявка принята, ожидается оплата"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }

            post("/{id}/reject") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Отсутствует ID слота", code = 400)
                    )

                    val errorMessage = slotService.rejectSlotRequest(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Заявка отклонена, слот снова свободен"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }

            post("/{id}/complete") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Отсутствует ID слота", code = 400)
                    )

                    val errorMessage = slotService.completeSlot(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Занятие успешно завершено!"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }

            post("/{slotId}/cancel") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()

                    val slotId = call.parameters["slotId"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Не указан ID слота", code = 400)
                    )

                    val errorMessage = slotService.cancelSlotByMentee(userId, slotId)

                    if (errorMessage == null) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Бронь успешно отменена, слот снова свободен")
                        )
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

            post("/{id}/link") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Отсутствует ID слота", code = 400)
                    )
                    val request = call.receive<AttachMeetingLinkRequest>()

                    val errorMessage = slotService.attachMeetingLink(userId, slotId, request.meetingLink)

                    if (errorMessage == null) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Ссылка на созвон успешно сохранена!"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = errorMessage, code = 400))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка сервера", code = 500)
                    )
                }
            }

            get {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slots = slotService.getSlotsForExpert(userId)

                    call.respond(HttpStatusCode.OK, slots)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка получения списка слотов", code = 500)
                    )
                }
            }

            get("/mentor/{mentorId}") {
                try {
                    val mentorId = call.parameters["mentorId"]
                    if (mentorId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Отсутствует ID ментора", code = 400)
                        )
                        return@get
                    }

                    val availableSlots = slotService.getAvailableSlotsForMentor(mentorId)
                    call.respond(HttpStatusCode.OK, availableSlots)

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка получения расписания", code = 500)
                    )
                }
            }

            get("/my") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val mySlots = slotService.getSlotsForMentee(userId)

                    call.respond(HttpStatusCode.OK, mySlots)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка получения расписания", code = 500)
                    )
                }
            }

            delete("/{id}") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val slotId = call.parameters["id"]

                    if (slotId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Отсутствует ID слота", code = 400)
                        )
                        return@delete
                    }

                    val success = slotService.deleteSlot(userId, slotId)

                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Слот успешно удален"))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(
                                error = "Слот не найден, не принадлежит вам или " +
                                    "уже заблокирован/оплачен (статус не FREE)",
                                code = 404
                            )
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Неверный формат ID слота", code = 400)
                    )
                }
            }

            @Suppress("TooGenericExceptionCaught")
            post("/{id}/payment-link") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Не авторизован", code = 401))

                val slotId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Неверный ID слота", code = 400))

                try {
                    // Здесь в будущем будет реальный запрос к ЮKassa.
                    // Пока мы генерируем фейковую ссылку для фронтенда и передаем в нее ID слота,
                    // чтобы фронтенд мог сымитировать успешную оплату.
                    val fakePaymentUrl = "https://dummy-payment-gateway.com/pay?slotId=$slotId&amount=1500"

                    call.respond(HttpStatusCode.OK, mapOf(
                        "paymentUrl" to fakePaymentUrl,
                        "status" to "PENDING"
                    ))
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "Ошибка при генерации ссылки", code = 500))
                }
            }
        }

        @Suppress("TooGenericExceptionCaught")
        post("/webhook/payment") {
            try {
                val requestParams = call.receive<Map<String, String>>()
                val slotId = requestParams["slotId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val secretKey = requestParams["secret"]

                if (secretKey != "my_super_secret_webhook_key_123") {
                    return@post call.respond(HttpStatusCode.Forbidden, "Неверный ключ безопасности")
                }

                val result = slotService.confirmPayment(slotId)

                if (result == null) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Платеж успешно обработан"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to result))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера")
            }
        }
    }
}
