package com.example.features.mentor

import kotlinx.serialization.Serializable


@Serializable
data class MentorProfileResponse(
    val id: Int,
    val userId: Int,
    val name: String,
    val competencies: List<String>, // Список тегов, например: ["IT", "Kotlin"]
    val experienceDescription: String,
    val hourlyRate: Int // Цена за час в рублях
)
@Serializable
data class MentorProfileRequest(
    val userId: Int,
    val name: String,
    val competencies: List<String>,
    val experienceDescription: String,
    val hourlyRate: Int
)