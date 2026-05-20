package com.example.features.slots

import com.example.core.dbQuery
import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.util.*

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

        } catch (e: Exception) {
            e.printStackTrace()
            return@dbQuery "Внутренняя ошибка сервера или базы данных"
        }
    }

    suspend fun getSlotsForExpert(userIdFromToken: String): List<SlotResponse> = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val expertProfileRow = ExpertProfiles
                .select { ExpertProfiles.userId eq userUuid }
                .singleOrNull() ?: return@dbQuery emptyList()

            val realProfileId = expertProfileRow[ExpertProfiles.id].value

            // Используем LEFT JOIN, чтобы подтянуть данные Менти, если слот занят
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
                        startTime = row[Slots.startTime].toString(),
                        endTime = row[Slots.endTime].toString(),
                        status = row[Slots.status]
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
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
        } catch (e: Exception) {
            e.printStackTrace()
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

            Slots.update({ Slots.id eq slotUuid }) {
                it[status] = "REQUESTED"
                it[menteeId] = userUuid
                it[updatedAt] = LocalDateTime.now()
            }
            return@dbQuery null
        } catch (e: Exception) {
            e.printStackTrace()
            return@dbQuery "Неверный формат ID слота"
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
        } catch (e: Exception) {
            e.printStackTrace()
            return@dbQuery "Ошибка сервера"
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
        } catch (e: Exception) {
            e.printStackTrace()
            return@dbQuery "Ошибка сервера"
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
                        startTime = row[Slots.startTime].toString(),
                        endTime = row[Slots.endTime].toString(),
                        status = row[Slots.status]
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
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
                it[status] = "BOOKED"
                it[updatedAt] = LocalDateTime.now()
            }

            return@dbQuery null
        } catch (e: Exception) {
            e.printStackTrace()
            return@dbQuery "Неверный формат ID слота"
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
                        status = row[Slots.status]
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

