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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Репозиторий Audius API
    private val repo = AudiusRepository(RetrofitInstance.api)

    // Realm-БД для SavedTrack
    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(schema = setOf(SavedTrack::class))
                .name("musicapp.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    /* ---------- StateFlows ---------- */

    private val _recentAlbums   = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val recentAlbums:   StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _dailyAlbums    = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val dailyAlbums:    StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val featuredTracks: StateFlow<List<Track>> = _featuredTracks

    private val _isLoading      = MutableStateFlow(false)
    val isLoading:      StateFlow<Boolean> = _isLoading

    /** Загружает Recently, Daily Mix и Featured */
    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                /* ---------- 1. Recently Played  ---------- */
                val savedList = realm
                    .query<SavedTrack>("userId == $0", userId)
                    .sort("playedAt", Sort.DESCENDING)
                    .find()

                val recentTemp = savedList
                    .groupBy { it.trackUserId }                      // один альбом на артиста
                    .map { (_, list) ->
                        val first = list.first()
                        AlbumDisplay(
                            userId   = first.trackUserId,
                            title    = first.artist,
                            coverUrl = first.imageUrl
                        )
                    }

                /* ---------- 2. Daily Mix  ---------- */
                val deferredLists = recentTemp.map { album ->
                    async { repo.searchTracks(album.title) }         // похожие треки
                }
                val allMixTracks = deferredLists.flatMap { it.await() }

                val dailyTemp = allMixTracks
                    .groupBy { it.user.id }                          // по артисту
                    .map { (aid, list) ->
                        AlbumDisplay(
                            userId   = aid,
                            title    = list.first().user.name,
                            coverUrl = list.first().artwork?.`150x150`
                        )
                    }
                    .filter { it.userId !in recentTemp.map { r -> r.userId } }  // исключаем повторы

                /* ---------- 3. Убираем повторы из recent ---------- */
                val recentUnique = recentTemp.filter { album ->
                    album.userId !in dailyTemp.map { d -> d.userId }
                }

                /* ---------- 4. Featured ---------- */
                val featured = repo.searchTracks("electronic")

                /* ---------- 5. Publish ---------- */
                _recentAlbums.value   = recentUnique
                _dailyAlbums.value    = dailyTemp
                _featuredTracks.value = featured

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
