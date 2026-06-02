package com.example.utils

import com.example.features.availability.AvailabilityWindows
import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import com.example.features.consultations.Consultations
import com.example.features.reviews.Reviews
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

  val jdbcUrl: String get() = postgresContainer.jdbcUrl
  val username: String get() = postgresContainer.username
  val password: String get() = postgresContainer.password

  fun init() {
    if (isInitialized) return

    postgresContainer.start()

    System.setProperty("DATABASE_URL", postgresContainer.jdbcUrl)
    System.setProperty("DATABASE_USER", postgresContainer.username)
    System.setProperty("DATABASE_PASSWORD", postgresContainer.password)

    Database.connect(
      url = postgresContainer.jdbcUrl,
      driver = "org.postgresql.Driver",
      user = postgresContainer.username,
      password = postgresContainer.password,
    )

    transaction {
      SchemaUtils.create(
        Users,
        ExpertProfiles,
        Competences,
        ExpertCompetences,
        Reviews,
        AvailabilityWindows,
        Consultations,
      )
    }

    isInitialized = true
  }

  fun clearTables() {
    transaction {
      Consultations.deleteAll()
      AvailabilityWindows.deleteAll()
      Reviews.deleteAll()
      ExpertCompetences.deleteAll()
      Competences.deleteAll()
      ExpertProfiles.deleteAll()
      Users.deleteAll()
    }
  }
}
