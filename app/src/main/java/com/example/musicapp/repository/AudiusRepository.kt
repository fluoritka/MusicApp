package com.example.musicapp.repository

import com.example.musicapp.model.Track
import com.example.musicapp.network.AudiusService

// Репозиторий для работы с AudiusService
class AudiusRepository(private val api: AudiusService) {

    // Поиск треков по ключевому запросу
    suspend fun searchTracks(query: String): List<Track> =
        api.searchTracks(query).data

    // Получение списка треков пользователя по ID
    suspend fun getUserTracks(userId: String): List<Track> =
        api.getUserTracks(userId).data
}
