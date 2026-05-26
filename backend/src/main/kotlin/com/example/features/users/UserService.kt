package com.example.features.users

import com.example.core.dbQuery
import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

@Suppress("TooGenericExceptionCaught", "SwallowedException")
class UserService {
  suspend fun registerUser(request: CreateUserRequest): UUID? =
    dbQuery {
      try {
        val securedPassword = BCrypt.hashpw(request.passwordHash, BCrypt.gensalt(12))

        val newUserID =
          Users
            .insertAndGetId {
              it[email] = request.email
              it[passwordHash] = securedPassword
              it[authProvider] = request.authProvider
              it[fullName] = request.fullName
              it[role] = request.role
            }.value

        if (request.role == "Mentor") {
          ExpertProfiles.insert {
            it[userId] = newUserID
            it[education] = request.education
            it[experienceDescription] = request.experienceDescription ?: ""
            it[hourlyRate] = request.hourlyRate?.toBigDecimal() ?: 0.0.toBigDecimal()
          }
        }

        newUserID
      } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
        null
      } catch (e: IllegalArgumentException) {
        null
      }
    }

  suspend fun getMentorById(mentorID: String): DetailedMentorResponse? {
    return dbQuery {
      val uuid = UUID.fromString(mentorID)

      val mentorRow =
        (Users innerJoin ExpertProfiles)
          .select { Users.id eq uuid }
          .singleOrNull() ?: return@dbQuery null

      val actualProfileId = mentorRow[ExpertProfiles.id]

      val competencesList =
        (ExpertCompetences innerJoin Competences)
          .select { ExpertCompetences.expertId eq actualProfileId }
          .map { row ->
            CompetenceResponse(
              id = row[Competences.id].value,
              name = row[Competences.name],
            )
          }

      DetailedMentorResponse(
        id = mentorRow[Users.id].value.toString(),
        fullName = mentorRow[Users.fullName],
        email = mentorRow[Users.email],
        education = mentorRow[ExpertProfiles.education] ?: "",
        experienceDescription = mentorRow[ExpertProfiles.experienceDescription],
        hourlyRate = mentorRow[ExpertProfiles.hourlyRate].toDouble(),
        averageRating = mentorRow[ExpertProfiles.averageRating],
        contactInfo = mentorRow[Users.contactInfo],
        competences = competencesList,
      )
    }
  }

  suspend fun authenticate(request: LoginRequest): Pair<String, String>? =
    dbQuery {
      val userRow =
        Users
          .select {
            Users.email eq request.email
          }.singleOrNull()

      if (userRow != null) {
        val storedHash = userRow[Users.passwordHash]
        val incomingPassword = request.passwordHash

        if (BCrypt.checkpw(incomingPassword, storedHash)) {
          return@dbQuery Pair(userRow[Users.id].value.toString(), userRow[Users.role])
        }
      }
      return@dbQuery null
    }

  suspend fun updateUserProfile(
    userIdFromToken: String,
    request: UpdateProfileRequest,
  ): Boolean =
    dbQuery {
      try {
        val userUuid = UUID.fromString(userIdFromToken)
        var usersUpdated = 0
        var expertUpdated = 0

        if (!request.newFullName.isNullOrBlank() || request.contactInfo != null) {
          usersUpdated =
            Users.update({ Users.id eq userUuid }) {
              if (!request.newFullName.isNullOrBlank()) {
                it[Users.fullName] = request.newFullName
              }
              if (request.contactInfo != null) {
                it[Users.contactInfo] = request.contactInfo
              }
            }
        }

        if (request.newExperienceDescription != null ||
          request.newHourlyRate != null ||
          request.newEducation != null
        ) {
          expertUpdated =
            ExpertProfiles.update({ ExpertProfiles.userId eq userUuid }) {
              if (request.newExperienceDescription != null) {
                it[ExpertProfiles.experienceDescription] = request.newExperienceDescription
              }
              if (request.newHourlyRate != null) {
                it[ExpertProfiles.hourlyRate] = request.newHourlyRate.toBigDecimal()
              }
              if (request.newEducation != null) {
                it[ExpertProfiles.education] = request.newEducation
              }
            }
        }
        usersUpdated > 0 || expertUpdated > 0
      } catch (e: IllegalArgumentException) {
        false
      } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
        false
      }
    }

  suspend fun deleteExpertRole(userIdFromToken: String): Boolean =
    dbQuery {
      try {
        val userUuid = UUID.fromString(userIdFromToken)

        val expertProfileRow =
          ExpertProfiles.select { ExpertProfiles.userId eq userUuid }.singleOrNull() ?: return@dbQuery false
        val realProfileId = expertProfileRow[ExpertProfiles.id]

        // Очищаем теги перед удалением профиля
        ExpertCompetences.deleteWhere { expertId eq realProfileId }

        // Удаляем сам профиль
        val deletedRows = ExpertProfiles.deleteWhere { userId eq userUuid }

        if (deletedRows > 0) {
          Users.update({ Users.id eq userUuid }) {
            it[role] = "Mentee"
          }
          return@dbQuery true
        }
        false
      } catch (e: IllegalArgumentException) {
        false
      } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
        false
      }
    }

  suspend fun getMentors(
    page: Int,
    pageSize: Int,
  ): PaginatedResponse<MentorCardResponse> =
    dbQuery {
      val query =
        (Users innerJoin ExpertProfiles)
          .select { Users.role eq "Mentor" }

      val totalCount = query.count()
      val totalPages = ((totalCount + pageSize - 1) / pageSize).toInt()

      val offset = ((page - 1) * pageSize).toLong()

      val items =
        query
          .limit(pageSize, offset)
          .map { row ->
            MentorCardResponse(
              id = row[Users.id].value.toString(),
              fullName = row[Users.fullName],
              hourlyRate = row[ExpertProfiles.hourlyRate].toDouble(),
              averageRating = row[ExpertProfiles.averageRating],
              contactInfo = row[Users.contactInfo],
              experienceDescription = row[ExpertProfiles.experienceDescription].take(100),
            )
          }

      PaginatedResponse(
        items = items,
        totalCount = totalCount,
        page = page,
        pageSize = pageSize,
        totalPages = totalPages,
      )
    }

  suspend fun updateAvatarUrl(
    userId: String,
    photoUrl: String,
  ): Boolean =
    dbQuery {
      Users.update({ Users.id eq UUID.fromString(userId) }) {
        it[Users.photoUrl] = photoUrl
      } > 0
    }
}
