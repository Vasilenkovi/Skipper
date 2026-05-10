package com.example.features.competences


import com.example.features.users.ExpertProfiles
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class CompetenceService {

    fun addCompetenceToExpert(request: AddCompetenceRequest): Boolean {
        return transaction {
            try{
                ExpertProfiles.insert {
                    it[ExpertCompetences.expertId] = UUID.fromString(request.expertProfileId)
                    it[ExpertCompetences.competenceId]=request.competenceId
                }
                true
            } catch (e: Exception){
                e.printStackTrace()
                false
            }

        }
    }
}
