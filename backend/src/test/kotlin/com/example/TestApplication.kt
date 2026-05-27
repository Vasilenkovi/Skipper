@file:Suppress("UnusedPrivateProperty", "WildcardImport")

package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.features.users.tables.Users
import com.example.features.competences.tables.Competences
import com.example.features.slots.tables.Slots
import com.example.features.reviews.tables.Reviews

// Функция для инициализации тестовой БД (в памяти H2)
fun createTestDatabase(): Database {
  return Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
    driver = "org.h2.Driver",
    user = "test",
    password = ""
  )
}

fun testApplicationWithDb(block: suspend TestApplicationEngine.() -> Unit) {
  val db = createTestDatabase()
  
  transaction(db) {
    SchemaUtils.create(Users, Competences, Slots, Reviews)
  }
  
  try {
    testApplication {
      application {
        configureSerialization()
        configureRouting()
        configureSecurity()
      }
      block()
    }
  } finally {
    // Чистим БД после теста
    transaction(db) {
      SchemaUtils.drop(Users, Competences, Slots, Reviews)
    }
  }
}
