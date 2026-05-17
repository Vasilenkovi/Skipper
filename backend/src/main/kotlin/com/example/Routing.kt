package com.example


import com.example.core.DatabaseFactory
import com.example.features.competences.CompetenceService
import com.example.features.competences.competenceRoutes
import com.example.features.users.UserService
import com.example.features.users.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    DatabaseFactory.init()

    routing {
        val userService = UserService()
        val competenceService = CompetenceService()

        userRoutes(userService)
        competenceRoutes(competenceService)
    }
}
