// app/src/main/java/com/example/musicapp/viewmodel/PlaylistViewModel.kt
package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.FavoriteTrack
import com.example.musicapp.model.Playlist
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import com.example.musicapp.session.SessionManager
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PlaylistViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(
                schema = setOf(
                    Playlist::class,
                    FavoriteTrack::class,
                    SavedTrack::class
                )
            )
                .name("library.realm")
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _favorites = MutableStateFlow<List<FavoriteTrack>>(emptyList())
    val favorites: StateFlow<List<FavoriteTrack>> = _favorites

    init {
        refresh()
    }

    fun createPlaylist(title: String) {
        val uid = SessionManager.currentUserId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                copyToRealm(
                    Playlist().apply {
                        id        = UUID.randomUUID().toString()
                        userId    = uid
                        this.title= title
                        createdAt = System.currentTimeMillis()
                    }
                )
            }
            refresh()
        }
    }

    fun addTrackToPlaylist(track: Track, playlistId: String) {
        val uid = SessionManager.currentUserId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val pl = query<Playlist>(
                    "id == $0 AND userId == $1",
                    playlistId, uid
                ).first().find() ?: return@write

                if (pl.tracks.any { it.id == track.id }) return@write

                val snap = copyToRealm(
                    SavedTrack().apply {
                        id          = track.id
                        trackUserId = track.user.id
                        title       = track.title
                        artist      = track.user.name
                        imageUrl    = track.artwork?.`150x150`
                        streamUrl   = track.streamUrl
                        userId      = uid
                        playedAt    = System.currentTimeMillis()
                    }
                )
                pl.tracks.add(snap)
            }
            refresh()
        }
    }

    fun toggleFavorite(track: Track) {
        val uid = SessionManager.currentUserId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val favId = "${track.id}_$uid"
                val existing = query<FavoriteTrack>(
                    "id == $0", favId
                ).first().find()
                if (existing != null) delete(existing)
                else copyToRealm(
                    FavoriteTrack().apply {
                        id       = favId
                        trackId  = track.id
                        userId   = uid
                        title    = track.title
                        artist   = track.user.name
                        imageUrl = track.artwork?.`150x150`
                        addedAt  = System.currentTimeMillis()
                    }
                )
            }
            refresh()
        }
    }

    private fun refresh() {
        val uid = SessionManager.currentUserId ?: return
        viewModelScope.launch {
            val (pls, favs) = withContext(Dispatchers.IO) {
                val p = realm.query<Playlist>("userId == $0", uid)
                    .sort("createdAt", Sort.DESCENDING)
                    .find()
                val f = realm.query<FavoriteTrack>("userId == $0", uid)
                    .sort("addedAt", Sort.DESCENDING)
                    .find()
                Pair(p, f)
            }
            _playlists.value = pls
            _favorites.value = favs
        }
    }

    override fun onCleared() {
        realm.close()
        super.onCleared()
    }
}
