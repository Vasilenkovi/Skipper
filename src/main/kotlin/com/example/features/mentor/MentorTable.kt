package com.example.features.mentor

import org.jetbrains.exposed.sql.Table

object MentorProfilesTable : Table("mentor_profiles") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val name = varchar("name", 255)

    // Для простоты пока будем хранить список компетенций как строку через запятую
    // (например: "Backend,Kotlin,Docker")
    val competencies = varchar("competencies", 500)

    val experienceDescription = text("experience_description")
    val hourlyRate = integer("hourly_rate")

    override val primaryKey = PrimaryKey(id)
}