// app/src/main/java/com/example/musicapp/viewmodel/HomeViewModel.kt
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

    private val _recentTracks    = MutableStateFlow<List<SavedTrack>>(emptyList())
    val   recentTracks  : StateFlow<List<SavedTrack>>   = _recentTracks

    private val _dailyAlbums     = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   dailyAlbums   : StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _recommendations = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   recommendations : StateFlow<List<AlbumDisplay>> = _recommendations

    // Новый StateFlow для одного "альбома" Recently Played
    private val _recentAlbums  = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val   recentAlbums : StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _isLoading      = MutableStateFlow(false)
    val   isLoading      : StateFlow<Boolean> = _isLoading

    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1) Загружаем последние 20 треков
                val recent20 = withContext(Dispatchers.IO) {
                    realm.query<SavedTrack>("userId == $0", userId)
                        .sort("playedAt", Sort.DESCENDING)
                        .limit(20)
                        .find()
                }

                // 2) Формируем Daily Mix
                val artistIds = recent20.map { it.trackUserId }.distinct().take(6)
                val mixes = artistIds.map { id ->
                    async(Dispatchers.IO) {
                        repo.getUserTracks(id)
                            .firstOrNull()
                            ?.let { first ->
                                AlbumDisplay(
                                    userId   = id,
                                    title    = "${first.user.name} Mix",
                                    coverUrl = first.artwork?.`150x150`
                                )
                            }
                    }
                }.mapNotNull { it.await() }

                // 3) Формируем Today's Picks
                val todays = withContext(Dispatchers.IO) {
                    repo.searchTracks("electronic")
                }
                val recs = todays.map { tr ->
                    AlbumDisplay(
                        userId   = tr.user.id,
                        title    = tr.title,
                        coverUrl = tr.artwork?.`150x150`
                    )
                }.take(20)

                // 4) Собираем единый альбом "Recently Played"
                val recentAlbum = recent20.firstOrNull()?.let { first ->
                    AlbumDisplay(
                        userId   = "recent",
                        title    = "Recently Played",
                        coverUrl = first.imageUrl
                    )
                }
                _recentAlbums.value = recentAlbum?.let { listOf(it) } ?: emptyList()

                // 5) Публикуем все данные
                _recentTracks.value    = recent20
                _dailyAlbums.value     = mixes
                _recommendations.value = recs

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
