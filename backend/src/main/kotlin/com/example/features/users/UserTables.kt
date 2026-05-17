package com.example.features.users

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object Users : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val authProvider = varchar("auth_provider", 50)
    val fullName = varchar("full_name", 255)
    val photoUrl = varchar("photo_url", 500).nullable()
    val contactInfo = varchar("contact_info", 255).nullable()
    val role = varchar("role", 50)
}

object ExpertProfiles : UUIDTable("expert_profiles") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val education = text("education").nullable()
    val experienceDescription = text("experience_description")
    val hourlyRate = decimal("hourly_rate", 10, 2)
    val averageRating = float("average_rating").default(0F)
}
