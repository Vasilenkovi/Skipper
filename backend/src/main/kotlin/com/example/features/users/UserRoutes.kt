package com.example.features.users

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoutes(userService: UserService) {

    route("/api/users") {

        post("/register") {
            try{

                val request = call.receive<CreateUserRequest>()

                val userId = userService.registerUser(request)

                if (userId != null){
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("user" to userId.toString(), "message" to "Пользователь успешно зарегистрирован")
                    )
                } else{
                    call.respond(HttpStatusCode.Conflict,
                        mapOf("error" to "Пользователь с таким email уже существует")
                    )
                }
            } catch(e: Exception){
                call.respond(HttpStatusCode.BadRequest,mapOf("error" to "Неверный формат данных: ${e.localizedMessage}"))
            }
        }
    }
}
