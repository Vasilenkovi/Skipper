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
    val hourlyRate: Double? = null,
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
    val experienceDescription: String?=null,
    val hourlyRate: Double? = null,
    val averageRating: Float? = null
)
