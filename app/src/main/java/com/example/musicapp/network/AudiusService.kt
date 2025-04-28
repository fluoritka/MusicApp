package com.example.musicapp.network

import com.example.musicapp.model.Track
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AudiusService {
    @GET("v1/tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("app_name") appName: String = "MusicApp"
    ): TrackSearchResponse

    @GET("v1/users/{user_id}/tracks")
    suspend fun getUserTracks(
        @Path("user_id") userId: String,
        @Query("app_name") appName: String = "MusicApp"
    ): TrackSearchResponse
}

data class TrackSearchResponse(
    val data: List<Track>
)
