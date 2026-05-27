//import kotlin.io.path.name
//
//// N:/SkipperNew/settings.gradle.kts
//rootProject.name = "SkipperNew"
//
//include(":mobile_change")
//project(":mobile_change").projectDir = file("mobile_change")
//

rootProject.name = "SkipperNew"

include(":mobile_change")project(":mobile_change").projectDir = file("mobile_change")

// Если внутри mobile_change есть свой модуль app (как я видел ранее):
include(":mobile_change:app")

