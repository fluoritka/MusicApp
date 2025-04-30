package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.AlbumDisplay
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val realm: Realm by lazy {
        val config = RealmConfiguration.Builder(
            schema = setOf(SavedTrack::class)
        )
            .name("musicapp.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.open(config)
    }

    private val _recentAlbums = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val recentAlbums: StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _dailyAlbums = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val dailyAlbums: StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val featuredTracks: StateFlow<List<Track>> = _featuredTracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Загружает «Recently Played» для данного userId */
    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val saved = realm.query<SavedTrack>("userId == $0", userId)
                    .sort("playedAt", Sort.DESCENDING)
                    .find()
                    .toList()

                _recentAlbums.value = saved
                    .groupBy { it.trackUserId }
                    .map { (_, list) ->
                        val first = list.first()
                        AlbumDisplay(
                            userId   = first.trackUserId,
                            title    = first.artist,
                            coverUrl = first.imageUrl
                        )
                    }

                // пока дублируем
                _dailyAlbums.value    = _recentAlbums.value
                _featuredTracks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
