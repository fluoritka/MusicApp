package com.example.musicapp.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Единый ExoPlayer-ViewModel для всего приложения,
 * теперь с поддержкой очереди и кнопок prev/next.
 */
class PlayerViewModel(app: Application) : AndroidViewModel(app) {

    /* ---------- low-level ---------- */
    private val exo = ExoPlayer.Builder(app).build()

    /* ---------- queue / index ---------- */
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue

    private val _index = MutableStateFlow(-1)          // -1 → нет трека
    val index: StateFlow<Int> = _index

    /* ---------- UI-state ---------- */
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    init {
        /* каждые 0.5 с обновляем progress */
        viewModelScope.launch {
            while (true) {
                val d = exo.duration.takeIf { it > 0 } ?: 1
                _progress.value = exo.currentPosition / d.toFloat()
                delay(500)
            }
        }
    }

    /* ---------- public API ---------- */

    /**
     * Проиграть [track] в контексте [queue].
     * Если queue не передана, используем текущую.
     */
    fun play(track: Track, queue: List<Track> = _queue.value) {
        val newIdx = queue.indexOfFirst { it.id == track.id }
        if (newIdx == -1) return

        if (queue !== _queue.value) _queue.value = queue
        if (newIdx != _index.value) {
            _index.value = newIdx
            start(track)
        } else {
            resume()
        }
    }

    fun toggle()      = if (_isPlaying.value) pause() else resume()
    fun seekTo(f:Float)= exo.seekTo((exo.duration * f).toLong())

    fun skipNext() {
        val next = _index.value + 1
        if (next < _queue.value.size) {
            _index.value = next
            start(_queue.value[next])
        }
    }

    fun skipPrevious() {
        val prev = _index.value - 1
        if (prev >= 0) {
            _index.value = prev
            start(_queue.value[prev])
        }
    }

    /* ---------- helpers ---------- */
    private fun start(tr: Track) {
        exo.stop(); exo.clearMediaItems()
        exo.setMediaItem(MediaItem.fromUri(tr.streamUrl))
        exo.prepare(); exo.play()
        _currentTrack.value = tr
        _isPlaying.value    = true
    }
    private fun pause()  { exo.pause(); _isPlaying.value = false }
    private fun resume() { exo.play();  _isPlaying.value = true }

    override fun onCleared() { exo.release(); super.onCleared() }
}
