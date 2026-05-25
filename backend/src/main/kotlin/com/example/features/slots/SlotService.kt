package com.example.features.slots

import com.example.core.dbQuery
import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.util.UUID

@Suppress("TooManyFunctions", "TooGenericExceptionCaught", "SwallowedException")
class SlotService {

    suspend fun createSlot(userIdFromToken: String, request: CreateSlotRequest): String? = dbQuery {
        try {
            val expertProfileRow = ExpertProfiles
                .select { ExpertProfiles.userId eq UUID.fromString(userIdFromToken) }
                .singleOrNull() ?: return@dbQuery "Профиль эксперта не найден"

            val realProfileId = expertProfileRow[ExpertProfiles.id].value

            val start = LocalDateTime.parse(request.startTime)
            val end = start.plusHours(1)

            if (start.isBefore(LocalDateTime.now())) {
                return@dbQuery "Нельзя создать слот на прошедшее время"
            }

            val hasOverlap = Slots.select {
                (Slots.expertId eq realProfileId) and
                    (Slots.startTime less end) and
                    (Slots.endTime greater start)
            }.any()

            if (hasOverlap) {
                return@dbQuery "У вас уже есть занятие, которое пересекается с этим временем"
            }

            Slots.insert {
                it[expertId] = realProfileId
                it[startTime] = start
                it[endTime] = end
                it[status] = "FREE"
                it[updatedAt] = LocalDateTime.now()
            }

            return@dbQuery null

        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun getSlotsForExpert(userIdFromToken: String): List<SlotResponse> = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val expertProfileRow = ExpertProfiles
                .select { ExpertProfiles.userId eq userUuid }
                .singleOrNull() ?: return@dbQuery emptyList()

            val realProfileId = expertProfileRow[ExpertProfiles.id].value

            (Slots leftJoin Users).select {
                Slots.expertId eq realProfileId
            }
                .orderBy(Slots.startTime to SortOrder.ASC)
                .map { row ->
                    SlotResponse(
                        id = row[Slots.id].value.toString(),
                        expertId = row[Slots.expertId].value.toString(),
                        menteeId = row[Slots.menteeId]?.value?.toString(),
                        menteeName = row[Users.fullName],
                        menteeEmail = row[Users.email],
                        meetingLink = row[Slots.meetingLink],
                        priceAtBooking = row[Slots.priceAtBooking]?.toDouble(),
                        startTime = row[Slots.startTime].toString(),
                        endTime = row[Slots.endTime].toString(),
                        status = row[Slots.status]
                    )
                }
        } catch (e: IllegalArgumentException) {
            emptyList()
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            emptyList()
        }
    }

    suspend fun deleteSlot(userIdFromToken: String, slotId: String): Boolean = dbQuery {
        try {
            val expertProfileRow = ExpertProfiles
                .select { ExpertProfiles.userId eq UUID.fromString(userIdFromToken) }
                .singleOrNull() ?: return@dbQuery false

            val realProfileId = expertProfileRow[ExpertProfiles.id].value

            val deletedRows = Slots.deleteWhere {
                (id eq UUID.fromString(slotId)) and
                    (expertId eq realProfileId) and
                    (status eq "FREE")
            }
            deletedRows > 0
        } catch (e: IllegalArgumentException) {
            false
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            false
        }
    }

    suspend fun bookSlot(userIdFromToken: String, slotId: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.status] != "FREE") {
                return@dbQuery "Этот слот уже занят или недоступен"
            }

            val expertProfile = ExpertProfiles.select { ExpertProfiles.id eq slotRow[Slots.expertId] }.singleOrNull()
            val currentPrice: java.math.BigDecimal? = expertProfile?.get(ExpertProfiles.hourlyRate)

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "PENDING"
                it[menteeId] = userUuid
                if (currentPrice != null) {
                    it[priceAtBooking] = currentPrice
                }
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun acceptSlotRequest(userIdFromToken: String, slotId: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val expertProfile = ExpertProfiles.select { ExpertProfiles.userId eq userUuid }.singleOrNull()
                ?: return@dbQuery "Профиль ментора не найден"
            val realProfileId = expertProfile[ExpertProfiles.id].value

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.expertId].value != realProfileId) return@dbQuery "Это не ваш слот"
            if (slotRow[Slots.status] != "REQUESTED") return@dbQuery "Слот не находится в статусе заявки"

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "BOOKED"
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun rejectSlotRequest(userIdFromToken: String, slotId: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val expertProfile = ExpertProfiles.select { ExpertProfiles.userId eq userUuid }.singleOrNull()
                ?: return@dbQuery "Профиль ментора не найден"
            val realProfileId = expertProfile[ExpertProfiles.id].value

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.expertId].value != realProfileId) return@dbQuery "Это не ваш слот"
            if (slotRow[Slots.status] != "REQUESTED") return@dbQuery "Слот не находится в статусе заявки"

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "FREE"
                it[menteeId] = null
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun getAvailableSlotsForMentor(mentorUserId: String): List<SlotResponse> = dbQuery {
        try {
            val expertProfileRow = ExpertProfiles
                .select { ExpertProfiles.userId eq UUID.fromString(mentorUserId) }
                .singleOrNull() ?: return@dbQuery emptyList()

            val realProfileId = expertProfileRow[ExpertProfiles.id].value

            Slots.select {
                (Slots.expertId eq realProfileId) and
                    (Slots.status eq "FREE") and
                    (Slots.startTime greaterEq LocalDateTime.now())
            }
                .orderBy(Slots.startTime to SortOrder.ASC)
                .map { row ->
                    SlotResponse(
                        id = row[Slots.id].value.toString(),
                        expertId = mentorUserId,
                        menteeId = null,
                        menteeName = null,
                        menteeEmail = null,
                        meetingLink = row[Slots.meetingLink],
                        priceAtBooking = row[Slots.priceAtBooking]?.toDouble(),
                        startTime = row[Slots.startTime].toString(),
                        endTime = row[Slots.endTime].toString(),
                        status = row[Slots.status]
                    )
                }
        } catch (e: IllegalArgumentException) {
            emptyList()
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            emptyList()
        }
    }

    suspend fun confirmPayment(slotId: String): String? = dbQuery {
        try {
            val slotUuid = UUID.fromString(slotId)

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.status] != "PENDING") {
                return@dbQuery "Оплатить можно только слоты в статусе ожидания (PENDING)"
            }

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "REQUESTED"
                it[updatedAt] = LocalDateTime.now()
            }

            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun getSlotsForMentee(userIdFromToken: String): List<MenteeSlotResponse> = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)

            (Slots innerJoin ExpertProfiles)
                .innerJoin(
                    otherTable = Users,
                    onColumn = { ExpertProfiles.userId },
                    otherColumn = { Users.id }
                )
                .select { Slots.menteeId eq userUuid }
                .orderBy(Slots.startTime to SortOrder.ASC)
                .map { row ->
                    MenteeSlotResponse(
                        id = row[Slots.id].value.toString(),
                        mentorName = row[Users.fullName],
                        startTime = row[Slots.startTime].toString(),
                        endTime = row[Slots.endTime].toString(),
                        status = row[Slots.status],
                        meetingLink = row[Slots.meetingLink],
                        priceAtBooking = row[Slots.priceAtBooking]?.toDouble()
                    )
                }
        } catch (e: IllegalArgumentException) {
            emptyList()
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            emptyList()
        }
    }

    suspend fun completeSlot(userIdFromToken: String, slotId: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val expertProfile = ExpertProfiles.select { ExpertProfiles.userId eq userUuid }.singleOrNull()
                ?: return@dbQuery "Профиль ментора не найден"
            val realProfileId = expertProfile[ExpertProfiles.id].value

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.expertId].value != realProfileId)
                return@dbQuery "Это не ваш слот"
            if (slotRow[Slots.status] != "BOOKED")
                return@dbQuery "Завершить можно только подтвержденное занятие (BOOKED)"


            if (LocalDateTime.parse(slotRow[Slots.endTime].toString()).isAfter(LocalDateTime.now())) {
                return@dbQuery "Занятие еще не закончилось"
            }

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "COMPLETED"
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun cancelSlotByMentee(userIdFromToken: String, slotId: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.menteeId] != userUuid) return@dbQuery "Это не ваша бронь"
            if (slotRow[Slots.status] == "COMPLETED") return@dbQuery "Завершенное занятие нельзя отменить"

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "FREE"
                it[menteeId] = null
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }

    suspend fun attachMeetingLink(userIdFromToken: String, slotId: String, link: String): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            val expertProfile = ExpertProfiles.select { ExpertProfiles.userId eq userUuid }.singleOrNull()
                ?: return@dbQuery "Профиль ментора не найден"
            val realProfileId = expertProfile[ExpertProfiles.id].value

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.expertId].value != realProfileId) return@dbQuery "Это не ваш слот"

            if (slotRow[Slots.status] == "FREE" || slotRow[Slots.status] == "COMPLETED") {
                return@dbQuery "Нельзя прикрепить ссылку к свободному или завершенному слоту"
            }

            Slots.update({ Slots.id eq slotUuid }) {
                it[meetingLink] = link
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: java.time.format.DateTimeParseException) {
            return@dbQuery "Неверный формат времени"
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }
}

