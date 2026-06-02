package com.example.features.consultations

import com.example.features.availability.AvailabilityWindows
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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

  fun getConsultationsForUser(userId: String): List<ConsultationResponse> =
    transaction {
      Consultations
        .selectAll()
        .where { (Consultations.expertId eq userId) or (Consultations.menteeId eq userId) }
        .map {
          ConsultationResponse(
            id = it[Consultations.id],
            expertId = it[Consultations.expertId],
            menteeId = it[Consultations.menteeId],
            startTime = it[Consultations.startTime].toString(),
            endTime = it[Consultations.endTime].toString(),
            status = it[Consultations.status],
            meetingLink = it[Consultations.meetingLink],
          )
        }
    }

  fun confirmConsultation(
    consultationId: Int,
    expertId: String,
  ): Boolean =
    transaction {
      Consultations.update({ (Consultations.id eq consultationId) and (Consultations.expertId eq expertId) }) {
        it[status] = "PLANNED"
      } > 0
    }

  fun cancelConsultation(
    consultationId: Int,
    userId: String,
  ): Boolean =
    transaction {
      Consultations.update({
        (Consultations.id eq consultationId) and
          ((Consultations.expertId eq userId) or (Consultations.menteeId eq userId))
      }) {
        it[status] = "CANCELLED"
      } > 0
    }

  fun attachMeetingLink(
    consultationId: Int,
    expertId: String,
    link: String,
  ): Boolean =
    transaction {
      Consultations.update({ (Consultations.id eq consultationId) and (Consultations.expertId eq expertId) }) {
        it[meetingLink] = link
      } > 0
    }
}
