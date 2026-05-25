package com.example.features.reviews

import com.example.core.dbQuery
import com.example.features.slots.Slots
import com.example.features.users.ExpertProfiles
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

@Suppress("TooGenericExceptionCaught", "SwallowedException")
class ReviewService {

    suspend fun leaveReview(userIdFromToken: String, slotId: String, request: CreateReviewRequest): String? = dbQuery {
        try {
            val userUuid = UUID.fromString(userIdFromToken)
            val slotUuid = UUID.fromString(slotId)

            if (request.rating !in 1..5) return@dbQuery "Рейтинг должен быть от 1 до 5"

            val slotRow = Slots.select { Slots.id eq slotUuid }.singleOrNull()
                ?: return@dbQuery "Слот не найден"

            if (slotRow[Slots.menteeId] != userUuid)
                return@dbQuery "Вы не можете оставить отзыв на чужое занятие"
            if (slotRow[Slots.status] != "COMPLETED")
                return@dbQuery "Оставить отзыв можно только после завершения занятия"

            val existingReview = Reviews.select { Reviews.slotId eq slotUuid }.singleOrNull()
            if (existingReview != null) return@dbQuery "Вы уже оставили отзыв на это занятие"

            val expertProfileId = slotRow[Slots.expertId].value

            Reviews.insert {
                it[Reviews.slotId] = slotUuid
                it[Reviews.menteeId] = userUuid
                it[Reviews.expertId] = expertProfileId
                it[Reviews.rating] = request.rating
                it[Reviews.comment] = request.comment
            }

            val allRatings = Reviews
                .select { Reviews.expertId eq expertProfileId }
                .map { it[Reviews.rating] }
            val count = allRatings.size
            val average = if (allRatings.isNotEmpty()) {
                Math.round(allRatings.average() * 10.0) / 10.0
            } else 0.0

            ExpertProfiles.update({ ExpertProfiles.id eq expertProfileId }) {
                it[averageRating] = average.toFloat()
                it[reviewsCount] = count
            }

            return@dbQuery null
        } catch (e: IllegalArgumentException) {
            return@dbQuery "Неверный формат ID"
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            return@dbQuery "Ошибка базы данных"
        }
    }
}
