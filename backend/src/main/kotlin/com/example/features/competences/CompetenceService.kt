package com.example.features.competences

import com.example.core.dbQuery
import com.example.features.users.ExpertProfiles
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import java.util.UUID

@Suppress("TooGenericExceptionCaught", "SwallowedException")
class CompetenceService {
  suspend fun addCompetenceToExpert(
    userIdFromToken: String,
    tagName: String,
  ): Boolean {
    return dbQuery {
      try {
        val existingTag =
          Competences
            .select { Competences.name eq tagName }
            .singleOrNull()

        val compId =
          if (existingTag != null) {
            existingTag[Competences.id].value
          } else {
            Competences
              .insertAndGetId {
                it[name] = tagName
              }.value
          }

        val expertProfileRow =
          ExpertProfiles
            .select { ExpertProfiles.userId eq UUID.fromString(userIdFromToken) }
            .singleOrNull()

        if (expertProfileRow == null) return@dbQuery false

        val realProfileId = expertProfileRow[ExpertProfiles.id].value

        val alreadyExist =
          ExpertCompetences
            .select {
              (ExpertCompetences.expertId eq realProfileId) and (ExpertCompetences.competenceId eq compId)
            }.any()

        if (alreadyExist) {
          return@dbQuery false
        }

        ExpertCompetences.insert {
          it[expertId] = realProfileId
          it[competenceId] = compId
        }

        return@dbQuery true
      } catch (e: IllegalArgumentException) {
        false
      } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
        false
      }
    }
  }

  suspend fun removeCompetenceFromExpert(
    userIdFromToken: String,
    tagName: String,
  ): Boolean =
    dbQuery {
      try {
        val expertProfileRow =
          ExpertProfiles
            .select { ExpertProfiles.userId eq UUID.fromString(userIdFromToken) }
            .singleOrNull() ?: return@dbQuery false

        val realProfileId = expertProfileRow[ExpertProfiles.id].value

        val competenceRow =
          Competences
            .select { Competences.name eq tagName }
            .singleOrNull() ?: return@dbQuery false

        val compId = competenceRow[Competences.id].value

        val deletedRows =
          ExpertCompetences.deleteWhere {
            (expertId eq realProfileId) and (competenceId eq compId)
          }

        deletedRows > 0
      } catch (e: IllegalArgumentException) {
        false
      } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
        false
      }
    }
}
