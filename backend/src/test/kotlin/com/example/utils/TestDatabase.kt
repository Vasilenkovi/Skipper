package com.example.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

// Используем паттерн Singleton (object в Kotlin), чтобы БД стартовала только один раз
object TestDatabase {

    // Инициализируем контейнер
    private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withDatabaseName("skipper_test")
        withUsername("test_user")
        withPassword("test_pass")
    }

    // Флаг, чтобы не инициализировать базу дважды
    private var isInitialized = false

    fun init() {
        if (isInitialized) return

        // 1. Стартуем Docker-контейнер
        postgresContainer.start()

        // 2. Подключаем JetBrains Exposed к нашему контейнеру
        Database.connect(
            url = postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )

        // 3. Создаем таблицы (перечисли здесь все объекты таблиц из твоего проекта)
        transaction {
            // Например: SchemaUtils.create(UsersTable, SlotsTable, ReviewsTable, CompetencesTable)
        }

        isInitialized = true
    }
    
    // Опционально: метод для очистки таблиц между тестами, если это необходимо
    fun clearTables() {
        transaction {
            // Например: UsersTable.deleteAll()
        }
    }
}
