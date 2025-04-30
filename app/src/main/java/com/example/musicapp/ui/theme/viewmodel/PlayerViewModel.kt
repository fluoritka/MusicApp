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

    /* -------- ExoPlayer -------- */
    private val exo = ExoPlayer.Builder(app).build()

    /* -------- Realm -------- */
    private val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(schema = setOf(SavedTrack::class))
                .name("musicapp.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    /* -------- UI-state -------- */
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val   queue: StateFlow<List<Track>> = _queue

    private val _index = MutableStateFlow(-1)
    val   index: StateFlow<Int> = _index

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val   currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val   isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress  = MutableStateFlow(0f)
    val   progress : StateFlow<Float> = _progress

    init {
        viewModelScope.launch {
            while (true) {
                val dur = exo.duration.takeIf { it > 0 } ?: 1
                _progress.value = exo.currentPosition / dur.toFloat()
                delay(500)
            }
        }
    }

    /* -------- public API -------- */
    fun play(track: Track, queue: List<Track> = _queue.value) {
        val newIdx = queue.indexOfFirst { it.id == track.id }
        if (newIdx == -1) return

        if (queue !== _queue.value) _queue.value = queue
        if (newIdx != _index.value) {
            _index.value = newIdx
            start(track)
        } else resume()
    }

    fun toggle()        = if (_isPlaying.value) pause() else resume()
    fun seekTo(f: Float)= exo.seekTo((exo.duration * f).toLong())

    fun skipNext() {
        val next = _index.value + 1
        if (next < _queue.value.size) {
            play(_queue.value[next])
        }
    }

    fun skipPrevious() {
        val prev = _index.value - 1
        if (prev >= 0) {
            play(_queue.value[prev])
        }
    }

    /* -------- helpers -------- */
    private fun start(tr: Track) {
        exo.stop(); exo.clearMediaItems()
        exo.setMediaItem(MediaItem.fromUri(tr.streamUrl))
        exo.prepare(); exo.play()

        _currentTrack.value = tr
        _isPlaying.value    = true

        /* --- сохраняем историю (IO) --- */
        SessionManager.currentUserId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                realm.write {
                    copyToRealm(
                        SavedTrack().apply {
                            id          = tr.id
                            title       = tr.title
                            artist      = tr.user.name
                            imageUrl    = tr.artwork?.`150x150`
                            userId      = uid
                            trackUserId = tr.user.id
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
