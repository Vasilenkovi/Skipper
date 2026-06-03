

rootProject.name = "SkipperNew"

include(":mobile_change")
project(":mobile_change").projectDir = file("mobile_change")

include(":app")
project(":app").projectDir = file("mobile_change/app")