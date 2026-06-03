//import kotlin.io.path.name
//
//// N:/SkipperNew/settings.gradle.kts
//rootProject.name = "SkipperNew"
//
//include(":mobile_change")
//project(":mobile_change").projectDir = file("mobile_change")
//
//
//rootProject.name = "SkipperNew"
//
//include(":mobile_change")project(":mobile_change").projectDir = file("mobile_change")
//
//// Если внутри mobile_change есть свой модуль app (как я видел ранее):
//include(":mobile_change:app")

rootProject.name = "SkipperNew"

// Подключаем проект mobile_change (если в нем есть build.gradle.kts)
include(":mobile_change")
project(":mobile_change").projectDir = file("mobile_change")

// Подключаем модуль app, который находится внутри mobile_change
include(":app")
project(":app").projectDir = file("mobile_change/app")