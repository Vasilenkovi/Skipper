package com.example.features.availability

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object AvailabilityWindows : Table("availability_windows") {
  val id = varchar("id", 36)
  val expertId = varchar("expert_id", 36)
  val startTime = datetime("start_time")
  val endTime = datetime("end_time")

  override val primaryKey = PrimaryKey(id)
}
