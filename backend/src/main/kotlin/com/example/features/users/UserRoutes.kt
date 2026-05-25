@file:Suppress("WildcardImport")
package com.example.features.users

import com.example.core.ErrorResponse
import com.example.core.JwtConfig
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@Suppress("LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught", "SwallowedException")
fun Route.userRoutes(userService: UserService) {

    route("/api/users") {

        post("/register") {
            try {
                val request = call.receive<CreateUserRequest>()
                val validationError = validateRateAndDescription(
                    request.hourlyRate,
                    request.experienceDescription
                )

                if (validationError != null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = validationError, code = 400))
                    return@post
                }

                val userId = userService.registerUser(request)

                if (userId != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("user" to userId.toString(), "message" to "Пользователь успешно зарегистрирован")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse(error = "Пользователь с таким email уже существует", code = 409)
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(error = "Неверный формат данных: ${e.localizedMessage}", code = 400)
                )
            }
        }

        get("/mentors") {
            try {
                val page = call.request.queryParameters["page"]?.toIntOrNull()?.takeIf { it > 0 } ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.takeIf { it > 0 } ?: 10

                val paginatedMentors = userService.getMentors(page, limit)
                call.respond(HttpStatusCode.OK, paginatedMentors)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Ошибка получения списка менторов", code = 500)
                )
            }
        }

        get("/mentors/{id}") {
            val id = call.parameters["id"]

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Отсутствует ID ментора", code = 400))
                return@get
            }

            try {
                val mentor = userService.getMentorById(id)
                if (mentor != null) {
                    call.respond(HttpStatusCode.OK, mentor)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Ментор не найден", code = 404))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Неверный формат ID", code = 400))
            }
        }

        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val userData = userService.authenticate(request)

                if (userData != null) {
                    val (userId, role) = userData
                    val token = JwtConfig.generateToken(userId, role)
                    call.respond(HttpStatusCode.OK, TokenResponse(token, userId, role))
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(error = "Неверный email или пароль", code = 401)
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Ошибка формата запроса", code = 400))
            }
        }

        authenticate {
            get("/profile") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val mentorProfile = userService.getMentorById(userId)

                    if (mentorProfile != null) {
                        call.respond(HttpStatusCode.OK, mentorProfile)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(error = "Профиль ментора не найден", code = 404)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка получения профиля", code = 500)
                    )
                }
            }

            put("/update-profile") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<UpdateProfileRequest>()

                    val validationError = validateRateAndDescription(
                        request.newHourlyRate,
                        request.newExperienceDescription
                    )

                    if (validationError != null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = validationError, code = 400))
                        return@put
                    }

                    val isUpdated = userService.updateUserProfile(userId, request)
                    if (isUpdated) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Профиль успешно обновлен"))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Не удалось обновить профиль", code = 400)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Ошибка формата запроса", code = 400))
                }
            }

            patch("/profile") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<UpdateProfileRequest>()

                    val isUpdated = userService.updateUserProfile(userId, request)

                    if (isUpdated) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Профиль успешно обновлен"))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Не удалось обновить профиль. Проверьте данные.", code = 400)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Внутренняя ошибка при обновлении профиля", code = 500)
                    )
                }
            }

            delete("/profile") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val success = userService.deleteExpertRole(userId)

                    if (success) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Вы больше не являетесь экспертом. Роль изменена на Менти.")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(error = "Профиль эксперта не найден", code = 404)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Ошибка при удалении профиля", code = 500)
                    )
                }
            }

            post("/avatar") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(error = "Не авторизован", code = 401)
                    )

                val multipart = call.receiveMultipart()
                var fileUrl: String? = null
                var errorMessage: String? = null

                val uploadDir = File("uploads/avatars")
                if (!uploadDir.exists()) uploadDir.mkdirs()

                withContext(Dispatchers.IO) {
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val ext = File(part.originalFileName ?: "").extension.lowercase()

                            if (ext !in listOf("jpg", "jpeg", "png")) {
                                errorMessage = "Допустимы только JPG и PNG"
                            } else {
                                val fileBytes = part.streamProvider().readBytes()
                                if (fileBytes.size > 2 * 1024 * 1024) {
                                    errorMessage = "Файл слишком большой (макс. 2Мб)"
                                } else {
                                    val fileName = "${UUID.randomUUID()}.$ext"
                                    File(uploadDir, fileName).writeBytes(fileBytes)
                                    fileUrl = "/uploads/avatars/$fileName"
                                }
                            }
                        }
                        part.dispose()
                    }
                }

                when {
                    errorMessage != null -> call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = errorMessage!!, code = 400)
                    )

                    fileUrl != null -> {
                        userService.updateAvatarUrl(userId, fileUrl!!)
                        call.respond(HttpStatusCode.OK, mapOf("photoUrl" to fileUrl))
                    }

                    else -> call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Файл не найден", code = 400))
                }
            }
        }
    }
}
