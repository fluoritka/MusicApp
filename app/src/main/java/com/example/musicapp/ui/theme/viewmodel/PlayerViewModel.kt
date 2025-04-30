// PlayerViewModel.kt
package com.example.musicapp.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.Track
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val exo = ExoPlayer.Builder(app).build()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    init {
        viewModelScope.launch {
            while(true) {
                val d = exo.duration
                _progress.value = if (d>0) exo.currentPosition/d.toFloat() else 0f
                delay(500)
            }
        }
    }

    fun play(track: Track) {
        if (_currentTrack.value?.id != track.id) {
            exo.stop(); exo.clearMediaItems()
            exo.setMediaItem(MediaItem.fromUri(track.streamUrl))
            exo.prepare()
            _currentTrack.value = track
        }
        exo.play(); _isPlaying.value = true
    }

    fun toggle() {
        if (_isPlaying.value) exo.pause().also{_isPlaying.value=false}
        else                  exo.play().also { _isPlaying.value=true }
    }

    override fun onCleared() {
        super.onCleared()
        exo.release()
    }
}
