pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Репозиторий для Spotify SDK (android-sdk)
        maven(url = "https://maven.pkg.github.com/spotify/android-sdk") {
            credentials {
                username = (extra["githubUsername"] as? String) ?: ""
                password = (extra["githubToken"] as? String) ?: ""
            }
        }
        // Репозиторий для Spotify Auth (если требуется)
        maven(url = "https://maven.pkg.github.com/spotify/android-auth") {
            credentials {
                username = (extra["githubUsername"] as? String) ?: ""
                password = (extra["githubToken"] as? String) ?: ""
            }
        }
    }
}

include(":app")
