@file:Suppress("WildcardImport")

package com.example

import com.example.features.slots.SlotCleanupWorker
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
  EngineMain.main(args)
}

fun Application.module() {
  configureSerialization()
  configureSecurity()
  configureRouting()

  SlotCleanupWorker.start()
}
