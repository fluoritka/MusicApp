plugins {
    id("com.android.application") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false // Изменено на 1.9.0
    id("org.jetbrains.compose") version "1.5.11" apply false
    id("io.realm.kotlin") version "1.12.0" apply false
}

// Добавьте репозитории на уровне проекта
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}