package com.example.features.competences

import kotlinx.serialization.Serializable

@Serializable
data class AddCompetenceRequest(
    val tagName: String
)
