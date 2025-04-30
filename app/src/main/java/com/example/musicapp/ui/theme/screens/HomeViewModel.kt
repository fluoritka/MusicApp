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

    // Репозиторий для запросов к Audius API
    private val repo = AudiusRepository(RetrofitInstance.api)

    // Realm для хранения SavedTrack
    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(schema = setOf(SavedTrack::class))
                .name("musicapp.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    private val _recentAlbums   = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val recentAlbums: StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _dailyAlbums    = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val dailyAlbums: StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val featuredTracks: StateFlow<List<Track>> = _featuredTracks

    private val _isLoading      = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Загружает все секции: Recently, Daily Mix и Featured */
    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1) Recently Played из локальной БД
                val savedList = realm
                    .query<SavedTrack>("userId == $0", userId)
                    .sort("playedAt", Sort.DESCENDING)
                    .find()
                    .toList()

                val recent = savedList
                    .groupBy { it.trackUserId }
                    .map { (_, list) ->
                        val first = list.first()
                        AlbumDisplay(
                            userId   = first.trackUserId,
                            title    = first.artist,
                            coverUrl = first.imageUrl
                        )
                    }
                _recentAlbums.value = recent

                // 2) Daily Mix: для каждого артиста из recent делаем поиск похожих треков
                val deferredLists = recent.map { album ->
                    async { repo.searchTracks(album.title) }
                }
                val allMixTracks = deferredLists.flatMap { it.await() }

                // Группируем по артисту и исключаем уже в recent
                val daily = allMixTracks
                    .groupBy { it.user.id to it.user.name }
                    .map { (key, list) ->
                        val (aid, name) = key
                        AlbumDisplay(
                            userId   = aid,
                            title    = name,
                            coverUrl = list.firstOrNull()?.artwork?.`150x150`
                        )
                    }
                    .filter { it.userId !in recent.map { r -> r.userId } }

                _dailyAlbums.value = daily

                // 3) Featured — просто пример
                _featuredTracks.value = repo.searchTracks("electronic")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
