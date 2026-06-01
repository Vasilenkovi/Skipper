package com.example.features.availability

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class AvailabilityService {
  @Suppress("TooGenericExceptionCaught", "SwallowedException", "ReturnCount")
  fun createWindow(
    expertId: String,
    request: CreateAvailabilityRequest,
  ): String? {
    return try {
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
      val start = LocalDateTime.parse(request.startTime, formatter)
      val end = LocalDateTime.parse(request.endTime, formatter)

      if (!end.isAfter(start)) {
        return "Время окончания должно быть позже времени начала"
      }

      if (start.isBefore(LocalDateTime.now())) {
        return "Нельзя создать окно доступности в прошлом"
      }

      transaction {
        AvailabilityWindows.insert {
          it[id] = UUID.randomUUID().toString()
          it[this.expertId] = expertId
          it[startTime] = start
          it[endTime] = end
        }
      }
      null // null означает успешное выполнение (нет ошибок)
    } catch (e: Exception) {
      "Неверный формат даты. Ожидался ISO 8601 (например: 2026-06-01T12:00:00)"
    }
  }
}
