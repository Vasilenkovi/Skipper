package com.example.features.mentor

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.mentorRouting() {

    route("/api/mentors") {

        // Первый эндпоинт: GET /api/mentors/{id}/profile
        get("/{id}/profile") {
            val mentorId = call.parameters["id"]?.toIntOrNull()

            if (mentorId == null) {
                call.respondText("Неверный ID ментора", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val profile = transaction {
                // Ищет запись, где id в таблице совпадает с mentorId из ссылки
                MentorProfilesTable
                    .selectAll()
                    .where { MentorProfilesTable.id eq mentorId }
                    .map { row ->
                        MentorProfileResponse(
                            id = row[MentorProfilesTable.id],
                            userId = row[MentorProfilesTable.userId],
                            name = row[MentorProfilesTable.name],
                            // Превращает строку "Дизайн,UX/UI" обратно в список тегов
                            competencies = row[MentorProfilesTable.competencies].split(","),
                            experienceDescription = row[MentorProfilesTable.experienceDescription],
                            hourlyRate = row[MentorProfilesTable.hourlyRate]
                        )
                    }
                    .singleOrNull()
            }

            if (profile == null) {
                call.respondText("Ментор с таким ID не найден", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(profile)
            }
        }
        post("/profile") {
            val request = call.receive<MentorProfileRequest>()

            // Открывает транзакцию в базе данных и записывает данные
            val newId = transaction {
                MentorProfilesTable.insert {
                    it[userId] = request.userId
                    it[name] = request.name
                    it[competencies] = request.competencies.joinToString(",")
                    it[experienceDescription] = request.experienceDescription
                    it[hourlyRate] = request.hourlyRate
                } get MentorProfilesTable.id
            }

            call.respondText("Профиль успешно создан! ID в базе: $newId", status = io.ktor.http.HttpStatusCode.Created)
        }
    }
}