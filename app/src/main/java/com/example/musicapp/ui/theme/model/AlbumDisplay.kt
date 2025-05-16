package com.example.musicapp.model

// Модель для отображения альбома в списке
data class AlbumDisplay(
    val userId: String,    // ID владельца или создателя альбома
    val title: String,     // заголовок альбома
    val coverUrl: String?  // URL обложки альбома (может отсутствовать)
)
