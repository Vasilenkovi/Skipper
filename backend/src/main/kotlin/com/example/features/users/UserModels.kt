package com.example.features.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val email: String,
    val passwordHash: String,
    val authProvider: String,
    val fullName: String,
    val role: String,
    val experienceDescription: String? = null,
    val hourlyRate: String? = null,
    val education: String? = null
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val photoUrl: String?,
    val contactInfo: String?,
    val role: String,
    val experienceDescription: String? = null,
    val hourlyRate: Double? = null,
    val averageRating: Float? = null
)

@Serializable
data class MentorCardResponse(
    val id: String,
    val fullName: String,
    val hourlyRate: Double,
    val averageRating: Float,
    val contactInfo: String?,
    val experienceDescription: String
)

@Serializable
data class CompetenceResponse(
    val id: Int, val name: String
)

@Serializable
data class DetailedMentorResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val contactInfo: String?,
    val education: String,
    val experienceDescription: String,
    val hourlyRate: Double,
    val averageRating: Float,
    val competences: List<CompetenceResponse>
)


@Serializable
data class LoginRequest(
    val email: String, val passwordHash: String
)

@Serializable
data class TokenResponse(
    val token: String, val userId: String, val role: String
)

@Serializable
data class UpdateProfileRequest(
    val newFullName: String? = null,
    val newExperienceDescription: String? = null,
    val newHourlyRate: String? = null,
    val newEducation: String? = null,
    val contactInfo: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val totalCount: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
