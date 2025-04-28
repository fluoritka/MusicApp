package com.example.musicapp.repository

import com.example.musicapp.model.Track
import com.example.musicapp.network.AudiusService

class AudiusRepository(private val api: AudiusService) {
    suspend fun searchTracks(query: String): List<Track> =
        api.searchTracks(query).data

    suspend fun getUserTracks(userId: String): List<Track> =
        api.getUserTracks(userId).data
}
