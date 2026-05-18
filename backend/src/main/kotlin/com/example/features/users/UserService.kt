package com.example.features.users

import com.example.core.dbQuery
import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.mindrot.jbcrypt.BCrypt

import java.util.*

class UserService {

    suspend fun registerUser(request: CreateUserRequest): UUID? {
        return dbQuery {
            try {
                val securedPassword = BCrypt.hashpw(request.passwordHash, BCrypt.gensalt(12))

                val newUserID = Users.insertAndGetId {
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
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getAllMentors(): List<MentorCardResponse> {
        return dbQuery {
            (Users innerJoin ExpertProfiles)
                .select { Users.role eq "Mentor" }
                .map { row ->
                    MentorCardResponse(
                        id = row[Users.id].value.toString(),
                        fullName = row[Users.fullName],
                        hourlyRate = row[ExpertProfiles.hourlyRate].toDouble(),
                        averageRating = row[ExpertProfiles.averageRating],
                        experienceDescription = row[ExpertProfiles.experienceDescription].take(100)
                    )
                }
        }
    }

    suspend fun getMentorById(mentorID: String): DetailedMentorResponse? {
        return dbQuery {
            val uuid = UUID.fromString(mentorID)

            val mentorRow = (Users innerJoin ExpertProfiles)
                .select { Users.id eq uuid }
                .singleOrNull() ?: return@dbQuery null

            val actualProfileId = mentorRow[ExpertProfiles.id]

            val competencesList = (ExpertCompetences innerJoin Competences)
                .select { ExpertCompetences.expertId eq actualProfileId }
                .map { row ->
                    CompetenceResponse(
                        id = row[Competences.id].value,
                        name = row[Competences.name]
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
                competences = competencesList
            )
        }
    }

    suspend fun authenticate(request: LoginRequest): Pair<String, String>? = dbQuery {
        val userRow = Users.select {
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

    suspend fun updateExpertProfile(mentorID: String, updateProfileRequest: UpdateProfileRequest) {
        return dbQuery {
            val uuid = UUID.fromString(mentorID)

            if (updateProfileRequest.newFullName != null) {
                Users.update({ Users.id eq uuid }) {
                    it[fullName] = updateProfileRequest.newFullName
                }
            }

            ExpertProfiles.update({ ExpertProfiles.userId eq uuid }) {
                updateProfileRequest.newEducation?.let { safeEdu -> it[education] = safeEdu }
                updateProfileRequest.newExperienceDescription?.let { safeExperienceDescription ->
                    it[experienceDescription] = safeExperienceDescription
                }
                updateProfileRequest.newHourlyRate?.let { safeHourlyRate ->
                    it[hourlyRate] = safeHourlyRate.toBigDecimal()
                }
            }
        }
    }

    suspend fun deleteExpertRole(userIdFromToken: String): Boolean = dbQuery {
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
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
