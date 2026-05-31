package com.example.utils

import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

object TestDatabase {
  private val postgresContainer =
    PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
      withDatabaseName("skipper_test")
      withUsername("test_user")
      withPassword("test_pass")
    }

  private var isInitialized = false

  fun init() {
    if (isInitialized) return

    postgresContainer.start()

    Database.connect(
      url = postgresContainer.jdbcUrl,
      driver = "org.postgresql.Driver",
      user = postgresContainer.username,
      password = postgresContainer.password,
    )

    transaction {
      SchemaUtils.create(Users, ExpertProfiles)
    }

    isInitialized = true
  }

  fun clearTables() {
    transaction {
      ExpertProfiles.deleteAll()
      Users.deleteAll()
    }
  }
}
