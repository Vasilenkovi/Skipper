@file:Suppress("UnusedPrivateProperty")

package com.example

import com.example.features.availability.AvailabilityWindows
import com.example.features.competences.Competences
import com.example.features.competences.ExpertCompetences
import com.example.features.consultations.Consultations
import com.example.features.reviews.Reviews
import com.example.features.slots.Slots
import com.example.features.users.ExpertProfiles
import com.example.features.users.Users
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun createTestDatabase(): Database =
  Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
    driver = "org.h2.Driver",
    user = "test",
    password = "",
  )

fun testApplicationWithDb(block: suspend ApplicationTestBuilder.() -> Unit) {
  val db = createTestDatabase()
  transaction(db) {
    SchemaUtils.create(
      Users,
      ExpertProfiles,
      Competences,
      ExpertCompetences,
      Slots,
      Reviews,
      AvailabilityWindows,
      Consultations,
    )
  }
  try {
    testApplication {
      environment {
        config = MapApplicationConfig()
      }
      application {
        configureSerialization()
        configureSecurity()
        configureRouting()
      }
      block()
    }
  } finally {
    transaction(db) {
      SchemaUtils.drop(
        Users,
        ExpertProfiles,
        Competences,
        ExpertCompetences,
        Slots,
        Reviews,
        AvailabilityWindows,
        Consultations,
      )
    }
  }
}
