package com.example.features.consultations

import kotlinx.serialization.Serializable

@Serializable
data class BookConsultationRequest(
  val expertId: String,
  val startTime: String,
  val endTime: String,
)

@Serializable
data class ConsultationResponse(
  val id: Int,
  val expertId: String,
  val menteeId: String,
  val startTime: String,
  val endTime: String,
  val status: String,
  val meetingLink: String? = null,
)
