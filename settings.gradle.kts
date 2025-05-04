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
//    versionCatalogs {
//        create("libs") {
//            version("koin", "3.5.3")
//
//            library("koin-android", "io.insert-koin", "koin-android").versionRef("koin")
//            library("koin-viewmodel", "io.insert-koin", "koin-androidx-viewmodel").versionRef("koin")
//            library("koin-workmanager", "io.insert-koin", "koin-androidx-workmanager").versionRef("koin")
//            library("koin-compose", "io.insert-koin", "koin-androidx-compose").versionRef("koin")
//        }
//    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "indrop"
include(":app")
include(":network")
include(":data")
