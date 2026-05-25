package com.example.features.reviews

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    val rating: Int,
    val comment: String? = null
)
