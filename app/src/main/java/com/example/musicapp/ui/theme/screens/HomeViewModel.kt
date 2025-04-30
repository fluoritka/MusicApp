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

    /* --------- зависимости --------- */
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

    /* --------- UI-state --------- */
    private val _recentTracks  = MutableStateFlow<List<SavedTrack>>(emptyList())
    val   recentTracks : StateFlow<List<SavedTrack>> = _recentTracks      // 20 треков

    private val _dailyAlbums   = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   dailyAlbums  : StateFlow<List<AlbumDisplay>> = _dailyAlbums     // миксы артистов

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val   featuredTracks : StateFlow<List<Track>> = _featuredTracks       // Today’s Picks

    private val _isLoading = MutableStateFlow(false)
    val   isLoading : StateFlow<Boolean> = _isLoading

    /* --------- публичный метод --------- */
    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                /* ---- IO-работа ---- */
                val (recent, mixes, featured) = withContext(Dispatchers.IO) {

                    /* 1. 20 последних прослушиваний */
                    val recent20 = realm
                        .query<SavedTrack>("userId == $0", userId)
                        .sort("playedAt", Sort.DESCENDING)
                        .limit(20)
                        .find()

                    /* 2. Daily Mix по исполнителям этих 20 треков */
                    val artistIds = recent20.map { it.trackUserId }.distinct().take(6)
                    val mixes = artistIds.map { id ->
                        async {
                            val tracks = repo.getUserTracks(id).take(60)
                            val first  = tracks.firstOrNull() ?: return@async null
                            AlbumDisplay(
                                userId   = id,
                                title    = "${first.user.name} Mix",
                                coverUrl = first.artwork?.`150x150`
                            )
                        }
                    }.mapNotNull { it.await() }

                    /* 3. Today’s Picks — просто топ-треков по жанру */
                    val todays = repo.searchTracks("electronic").take(60)

                    Triple(recent20, mixes, todays)
                }

                /* ---- публикуем ---- */
                _recentTracks.value  = recent
                _dailyAlbums.value   = mixes
                _featuredTracks.value= featured

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
