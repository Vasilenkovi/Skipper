package com.example.features.slots

import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object Slots : UUIDTable("slots") {
    val expertId = reference("expert_id", ExpertProfiles, onDelete = ReferenceOption.CASCADE)

    val menteeId = reference("mentee_id", Users, onDelete = ReferenceOption.SET_NULL).nullable()

    val txId = varchar("tx_id", 255).nullable()

    val startTime = datetime("start_time")
    val endTime = datetime("end_time")

    val status = varchar("status", 20).default("FREE")

    val updatedAt = datetime("updated_at")

    init {
        index(isUnique = false, status, startTime)
    }
}
