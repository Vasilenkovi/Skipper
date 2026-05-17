package com.example.features.competences

import com.example.features.users.ExpertProfiles
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class CompetenceService {

    fun addCompetenceToExpert(userIdFromToken: String, tagName: String): Boolean {
        return transaction {
            try {
                val existingTag = Competences
                    .select { Competences.name eq tagName }
                    .singleOrNull()

                val compId = if (existingTag != null) {
                    existingTag[Competences.id].value
                } else {
                    Competences.insertAndGetId {
                        it[name] = tagName
                    }.value
                }

                val expertProfileRow = ExpertProfiles
                    .select { ExpertProfiles.userId eq UUID.fromString(userIdFromToken) }
                    .singleOrNull()


                if (expertProfileRow == null) return@transaction false


                val realProfileId = expertProfileRow[ExpertProfiles.id].value

                val alreadyExist = ExpertCompetences
                    .select {
                        (ExpertCompetences.expertId eq realProfileId) and (ExpertCompetences.competenceId eq compId)
                    }.any()

                if (alreadyExist) {
                    return@transaction false
                }

                ExpertCompetences.insert {
                    it[expertId] = realProfileId
                    it[competenceId] = compId
                }

                return@transaction true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}
