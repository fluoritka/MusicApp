package com.example.musicapp.model

data class Track(
    val id: String,
    val title: String,
    val user: User,
    val artwork: Artwork?,
    private val overrideUrl: String? = null
) {
    /** Если при создании передан overrideUrl, используем его, иначе формируем стандартный URL */
    val streamUrl: String
        get() = overrideUrl ?: "https://api.audius.co/v1/tracks/$id/stream?app_name=MusicApp"
}

data class User(
    val id: String,
    val name: String
)

data class Artwork(
    val `150x150`: String?,
    val `480x480`: String?,
    val `1000x1000`: String?
)