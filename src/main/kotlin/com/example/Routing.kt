package com.example

import com.example.db.DatabaseFactory
import com.example.features.mentor.mentorRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    DatabaseFactory.init()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        mentorRouting()
    }
}