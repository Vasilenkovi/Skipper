package com.example.core

import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // Настраиваем подключение
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://127.0.0.1:5433/skipper_database"
            username = "skipper_admin"
            password = "oM2dX_PI1U"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        // Подключаемся
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Users, ExpertProfiles, Competences, ExpertCompetences)
        }
    }
}
