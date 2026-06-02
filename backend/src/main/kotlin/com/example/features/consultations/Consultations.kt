package com.example.features.consultations

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Consultations : Table("consultations") {
  val id = integer("id").autoIncrement()
  val expertId = varchar("expert_id", 36)
  val menteeId = varchar("mentee_id", 36)
  val startTime = datetime("start_time")
  val endTime = datetime("end_time")

  // Статусы: PENDING, AWAITING_PAYMENT, PLANNED, CANCELLED, COMPLETED
  val status = varchar("status", 30).default("PENDING")
  val meetingLink = varchar("meeting_link", 255).nullable()
  val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

  override val primaryKey = PrimaryKey(id)
}
