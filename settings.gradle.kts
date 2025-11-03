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

rootProject.name = "PartiuVer!"

include(":app")
include(":core:ui")
include(":core:network")
include(":data")
include(":domain")
include(":feature:search")
include(":feature:detail")
