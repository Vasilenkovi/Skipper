@file:Suppress("WildcardImport")
package com.example.features.slots

import com.example.core.dbQuery
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

@Suppress("TooGenericExceptionCaught")
object SlotCleanupWorker {
    private var job: Job? = null

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    cleanupExpiredRequests()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(3600_000L)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    private suspend fun cleanupExpiredRequests() = dbQuery {
        val thresholdTime = LocalDateTime.now().minusHours(24)

        val updatedCount = Slots.update({
            (Slots.status eq "REQUESTED") and (Slots.updatedAt less thresholdTime)
        }) {
            it[status] = "FREE"
            it[menteeId] = null
        }

        if (updatedCount > 0) {
            println("[Worker] Аннулировано просроченных заявок менторов: $updatedCount")
        }
    }
}
