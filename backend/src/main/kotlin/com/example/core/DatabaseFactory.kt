package com.example.core

import com.example.features.availability.AvailabilityWindows
import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import com.example.features.reviews.Reviews
import com.example.features.slots.Slots
import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
  fun init() {
    if (TransactionManager.isInitialized()) return

    val config =
      HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://127.0.0.1:5433/skipper_database?currentSchema=public"
        username = "skipper_admin"
        password = "oM2dX_PI1U"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
      }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
      SchemaUtils.createMissingTablesAndColumns(
        Users,
        ExpertProfiles,
        Competences,
        ExpertCompetences,
        Slots,
        Reviews,
        AvailabilityWindows,
      )
    }
  }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
