package com.example.features.slots

import kotlinx.serialization.Serializable

@Serializable
data class CreateSlotRequest(
    val startTime: String
)

@Serializable
data class SlotResponse(
    val id: String,
    val expertId: String,
    val menteeId: String?,
    val startTime: String,
    val endTime: String,
    val status: String
)
