package com.example.features.competences

import com.example.features.users.ExpertProfiles
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Competences : IntIdTable("competences") {
    val name = varchar("name",255).uniqueIndex()
}

object ExpertCompetences : Table("expert_competences") {
    val expertId = reference("expert_id", ExpertProfiles, onDelete = ReferenceOption.CASCADE)
    val competenceId = reference("competence_id", Competences, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(expertId, competenceId)
}
