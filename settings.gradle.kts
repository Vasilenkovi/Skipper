import kotlin.io.path.name

pluginManagement {
  repositories {
    google()
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
}

rootProject.name = "SkipperNew"

// Подключаем мобильное приложение
include(":app")
project(":app").projectDir = file("mobile_change/app")
