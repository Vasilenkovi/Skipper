@file:Suppress("UnusedPrivateProperty")

package com.example

import com.example.features.competences.tables.Competences
import com.example.features.reviews.tables.Reviews
import com.example.features.slots.tables.Slots
import com.example.features.users.tables.Users
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun createTestDatabase(): Database = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
    driver = "org.h2.Driver",
    user = "test",
    password = ""
)

fun testApplicationWithDb(block: suspend ApplicationTestBuilder.() -> Unit) {
    val db = createTestDatabase()
    transaction(db) {
        SchemaUtils.create(Users, Competences, Slots, Reviews)
    }
    try {
        testApplication {
            application {
                configureSerialization()
                configureRouting()
            }
            block()
        }
    } finally {
        transaction(db) {
            SchemaUtils.drop(Users, Competences, Slots, Reviews)
        }
    }
}
