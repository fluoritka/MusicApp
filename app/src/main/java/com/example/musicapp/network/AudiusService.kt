package com.example.musicapp.network

import com.example.musicapp.model.Track
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// API-интерфейс для запросов к сервису Audius
interface AudiusService {

    // Поиск треков по ключевой фразе
    @GET("v1/tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("app_name") appName: String = "MusicApp"
    ): TrackSearchResponse

    // Получение списка треков пользователя по его ID
    @GET("v1/users/{user_id}/tracks")
    suspend fun getUserTracks(
        @Path("user_id") userId: String,
        @Query("app_name") appName: String = "MusicApp"
    ): TrackSearchResponse
}

// Обёртка для ответа API с данными треков
data class TrackSearchResponse(
    val data: List<Track>
)
