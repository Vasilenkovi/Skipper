package com.example.features.slots

import kotlinx.serialization.Serializable

@Serializable
data class CreateSlotRequest(
  val startTime: String,
)

@Serializable
data class SlotResponse(
  val id: String,
  val expertId: String,
  val menteeId: String?,
  val menteeName: String?,
  val menteeEmail: String?,
  val startTime: String,
  val endTime: String,
  val meetingLink: String? = null,
  val priceAtBooking: Double? = null,
  val status: String,
)

@Serializable
data class MenteeSlotResponse(
  val id: String,
  val mentorName: String,
  val startTime: String,
  val endTime: String,
  val meetingLink: String? = null,
  val priceAtBooking: Double? = null,
  val status: String,
)

@Serializable
data class AttachMeetingLinkRequest(
  val meetingLink: String,
)
