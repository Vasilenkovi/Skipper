pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // Блок versionCatalogs удален, так как Gradle 
    // автоматически находит файл gradle/libs.versions.toml
}

rootProject.name = "SkipperNew"

// Подключаем мобильное приложение как модуль :app
include(":app")
project(":app").projectDir = file("mobile_change/app")
