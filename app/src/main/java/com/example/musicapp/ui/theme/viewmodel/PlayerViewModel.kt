// app/src/main/java/com/example/musicapp/ui/theme/viewmodel/PlayerViewModel.kt
package com.example.musicapp.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import com.example.musicapp.session.SessionManager
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(app: Application) : AndroidViewModel(app) {

    private val exo = ExoPlayer.Builder(app).build()

    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(schema = setOf(SavedTrack::class))
                .name("musicapp.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    private val _queue   = MutableStateFlow<List<Track>>(emptyList())
    val   queue: StateFlow<List<Track>> = _queue

    private val _index   = MutableStateFlow(-1)
    val   index: StateFlow<Int> = _index

    private val _current = MutableStateFlow<Track?>(null)
    val   currentTrack: StateFlow<Track?> = _current

    private val _isPlaying = MutableStateFlow(false)
    val   isPlaying:  StateFlow<Boolean> = _isPlaying

    private val _progress  = MutableStateFlow(0f)
    val   progress:  StateFlow<Float> = _progress

    init {
        viewModelScope.launch {
            while (true) {
                val dur = exo.duration.takeIf { it > 0 } ?: 1L
                _progress.value = exo.currentPosition / dur.toFloat()
                delay(500)
            }
        }
    }

    /** Обычное воспроизведение List<Track> из сети */
    fun play(track: Track, queue: List<Track> = _queue.value) {
        val newIdx = queue.indexOfFirst { it.id == track.id }
        if (newIdx == -1) return
        if (queue !== _queue.value) _queue.value = queue
        if (newIdx != _index.value) {
            _index.value = newIdx
            start(track)
        } else resume()
    }

    /** Воспроизведение сохранённых треков из БД */
    fun playSaved(saved: SavedTrack, savedQueue: List<SavedTrack> = listOf(saved)) {
        // преобразуем SavedTrack в модель Track для единого API
        val list = savedQueue.map {
            Track(
                id = it.id,
                title = it.title.orEmpty(),
                user = com.example.musicapp.model.User(it.trackUserId, it.artist.orEmpty()),
                artwork = com.example.musicapp.model.Artwork(it.imageUrl, it.imageUrl, it.imageUrl)
            )
        }
        play(
            list.first { it.id == saved.id },
            list
        )
    }

    fun toggle()        = if (_isPlaying.value) pause() else resume()
    fun seekTo(f: Float)= exo.seekTo((exo.duration * f).toLong())
    fun skipNext()      = if (_index.value + 1 < _queue.value.size) play(_queue.value[_index.value + 1])
    else Unit
    fun skipPrevious()  = if (_index.value - 1 >= 0) play(_queue.value[_index.value - 1])
    else Unit

    private fun start(tr: Track) {
        exo.stop(); exo.clearMediaItems()
        exo.setMediaItem(MediaItem.fromUri(tr.streamUrl))
        exo.prepare(); exo.play()
        _current.value   = tr
        _isPlaying.value = true

        SessionManager.currentUserId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                realm.write {
                    copyToRealm(
                        SavedTrack().apply {
                            id          = "${tr.id}_${System.currentTimeMillis()}"
                            trackUserId = tr.user.id
                            title       = tr.title
                            artist      = tr.user.name
                            imageUrl    = tr.artwork?.`150x150`
                            streamUrl   = tr.streamUrl
                            userId      = uid
                            playedAt    = System.currentTimeMillis()
                        }
                    )
                }
            }
        }
    }

    private fun pause()  { exo.pause(); _isPlaying.value = false }
    private fun resume() { exo.play();  _isPlaying.value = true }

    override fun onCleared() {
        exo.release()
        realm.close()
        super.onCleared()
    }
}
