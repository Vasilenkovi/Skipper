package com.example.features.consultations

import com.example.features.availability.AvailabilityWindows
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ConsultationService {
  @Suppress("TooGenericExceptionCaught", "SwallowedException", "ReturnCount")
  fun bookConsultation(
    menteeId: String,
    request: BookConsultationRequest,
  ): String? {
    return transaction {
      try {
        val start = LocalDateTime.parse(request.startTime)
        val end = LocalDateTime.parse(request.endTime)

        // 1. Ищем свободное окно доступности
        val window =
          AvailabilityWindows
            .selectAll()
            .where {
              (AvailabilityWindows.expertId eq request.expertId) and
                (AvailabilityWindows.startTime eq start) and
                (AvailabilityWindows.endTime eq end)
            }.singleOrNull()

        if (window == null) {
          return@transaction "Окно доступности не найдено или уже было забронировано"
        }

        // 2. Удаляем окно доступности, так как оно теперь занято
        AvailabilityWindows.deleteWhere {
          AvailabilityWindows.id eq window[AvailabilityWindows.id]
        }

        // 3. Создаем запись о консультации со статусом ожидания
        Consultations.insert {
          it[expertId] = request.expertId
          it[this.menteeId] = menteeId
          it[startTime] = start
          it[endTime] = end
          it[status] = "PENDING"
        }

        null // Успешное выполнение, возвращаем null (нет ошибок)
      } catch (e: Exception) {
        "Ошибка при бронировании: неверный формат даты или сбой БД"
      }
    }
  }
}
