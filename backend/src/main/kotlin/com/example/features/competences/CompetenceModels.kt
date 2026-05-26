package com.example.features.competences

import kotlinx.serialization.Serializable

@Serializable
data class AddCompetenceRequest(
  val tagName: String,
)

@Serializable
data class RemoveCompetenceRequest(
  val tagName: String,
)
