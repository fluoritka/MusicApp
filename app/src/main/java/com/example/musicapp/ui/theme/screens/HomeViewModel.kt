package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.AlbumDisplay
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    /* ---------- зависимости ---------- */

    private val repo = AudiusRepository(RetrofitInstance.api)

    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(schema = setOf(SavedTrack::class))
                .name("musicapp.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    /* ---------- UI-state ---------- */

    private val _recentAlbums   = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   recentAlbums:   StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _dailyAlbums    = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   dailyAlbums:    StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val   featuredTracks: StateFlow<List<Track>> = _featuredTracks

    private val _isLoading      = MutableStateFlow(false)
    val   isLoading:      StateFlow<Boolean> = _isLoading

    /* ---------- публичный метод ---------- */

    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                /* --- работаем на фоне --- */
                val result = withContext(Dispatchers.IO) {

                    /* === 1. Recently Played =================================================== */
                    val saved = realm
                        .query<SavedTrack>("userId == $0", userId)
                        .sort("playedAt", Sort.DESCENDING)
                        .find()

                    val recent = saved
                        .groupBy { it.trackUserId }            // один альбом на артиста
                        .values
                        .map { list ->
                            val first = list.first()
                            AlbumDisplay(
                                userId   = first.trackUserId,
                                title    = first.artist,
                                coverUrl = first.imageUrl
                            )
                        }

                    /* === 2. Daily Mix (похожие треки) ====================================== */
                    val mixes = recent.map { album ->
                        async { repo.searchTracks(album.title) }
                    }.flatMap { it.await() }

                    val daily = mixes
                        .groupBy { it.user.id }
                        .values
                        .map { list ->
                            AlbumDisplay(
                                userId   = list.first().user.id,
                                title    = list.first().user.name,
                                coverUrl = list.first().artwork?.`150x150`
                            )
                        }
                        .filter { it.userId !in recent.map { r -> r.userId } }

                    /* === 3. Featured ======================================================== */
                    val featured = repo.searchTracks("electronic")

                    Triple(recent, daily, featured)
                }

                /* --- публикуем результат на UI-потоке --- */
                _recentAlbums.value   = result.first
                _dailyAlbums.value    = result.second
                _featuredTracks.value = result.third

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        realm.close()
        super.onCleared()
    }
}
