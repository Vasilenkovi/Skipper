plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("io.gitlab.arturbosch.detekt") version "1.24.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21) 
}


ktlint {
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    coloredOutput.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

detekt {
    toolVersion = "1.24.0"
    config.setFrom(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    source.setFrom(files("src/main/kotlin", "src/test/kotlin"))
}
