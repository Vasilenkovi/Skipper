package com.example.features.users

import com.example.core.JwtConfig
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to validationError))
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
                        mapOf("error" to "Пользователь с таким email уже существует")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Неверный формат данных: ${e.localizedMessage}")
                )
            }
        }

        get("/mentors") {
            try {
                val mentors = userService.getAllMentors()

                call.respond(HttpStatusCode.OK, mentors)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Ошибка получения списка менторов"))
            }
        }

        get("/mentors/{id}") {
            val id = call.parameters["id"]

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing mentor ID"))
                return@get
            }

            try {
                val mentor = userService.getMentorById(id)
                if (mentor != null) {
                    call.respond(HttpStatusCode.OK, mentor)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Mentor not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Invalid ID format"))
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
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Неверный email или пароль"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Ошибка формата запроса"))
            }
        }
        authenticate {
            put("/update-profile") {
                try {
                    val token = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asString()
                    val request = call.receive<UpdateProfileRequest>()

                    val validationError = validateRateAndDescription(
                        request.newHourlyRate,
                        request.newExperienceDescription
                    )

                    if (validationError != null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to validationError))
                        return@put
                    }

                    userService.updateExpertProfile(token, request)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Профиль успешно обновлен"))

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, message = mapOf("error" to "Ошибка формата запроса"))
                }
            }
        }
    }
}
