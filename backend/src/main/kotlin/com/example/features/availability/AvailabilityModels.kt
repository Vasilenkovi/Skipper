package com.example.features.availability

import kotlinx.serialization.Serializable

@Serializable
data class CreateAvailabilityRequest(
  val startTime: String,
  val endTime: String,
)

@Serializable
data class AvailabilityResponse(
  val id: String,
  val expertId: String,
  val startTime: String,
  val endTime: String,
)
