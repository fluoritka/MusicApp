package com.example.musicapp.model

// Представляет аудиотрек из Audius API
data class Track(
    val id: String,
    val title: String,
    val user: User,
    val artwork: Artwork?,
    private val overrideUrl: String? = null
) {
    // URL для воспроизведения трека
    val streamUrl: String
        get() = overrideUrl ?: "https://api.audius.co/v1/tracks/$id/stream?app_name=MusicApp"
}

// Пользователь (исполнитель) трека
data class User(
    val id: String,
    val name: String
)

// Различные разрешения обложки трека
data class Artwork(
    val `150x150`: String?,
    val `480x480`: String?,
    val `1000x1000`: String?
)