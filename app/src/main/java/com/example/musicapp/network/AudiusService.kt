// app/src/main/java/com/example/musicapp/network/AudiusService.kt
package com.example.musicapp.network

import com.example.musicapp.model.Track
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс для запросов к API Audius.
 * Путь файла и package-ди­ректива должны совпадать: com/example/musicapp/network.
 */
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

/**
 * Обёртка для списка треков.
 */
data class TrackSearchResponse(
    val data: List<Track>
)
