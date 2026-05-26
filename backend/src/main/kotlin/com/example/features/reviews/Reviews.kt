package com.example.features.reviews

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Reviews : UUIDTable("reviews") {
  val slotId = uuid("slot_id").uniqueIndex() // Один слот = один отзыв
  val menteeId = uuid("mentee_id")
  val expertId = uuid("expert_id")
  val rating = integer("rating") // от 1 до 5
  val comment = text("comment").nullable()
  val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}
