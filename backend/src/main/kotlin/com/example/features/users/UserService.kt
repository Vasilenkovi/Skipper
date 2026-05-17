package com.example.features.users

import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserService {

    fun registerUser(request: CreateUserRequest): UUID? {
        return transaction {
            try {
                val newUserID = Users.insertAndGetId {
                    it[email] = request.email
                    it[passwordHash] = request.passwordHash
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

    fun getAllMentors(): List<MentorCardResponse> {
        return transaction {
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

    fun getMentorById(mentorID: String): DetailedMentorResponse? {
        return transaction {
            val uuid = UUID.fromString(mentorID)

            val mentorRow = (Users innerJoin ExpertProfiles)
                .select { Users.id eq uuid }
                .singleOrNull() ?: return@transaction null

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

    fun authenticate(request: LoginRequest): Pair<String, String>? {
        return transaction {
            val userRow = Users.select {
                (Users.email eq request.email) and (Users.passwordHash eq request.passwordHash)
            }.singleOrNull()

            if (userRow != null) {
                Pair(userRow[Users.id].value.toString(), userRow[Users.role])
            } else null
        }
    }

    fun updateExpertProfile(mentorID: String, updateProfileRequest: UpdateProfileRequest) {
        return transaction {
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
}
