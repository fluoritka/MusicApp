package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.AlbumDisplay
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repo = AudiusRepository(RetrofitInstance.api)

    private val _recentAlbums = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val recentAlbums: StateFlow<List<AlbumDisplay>> = _recentAlbums

    private val _dailyAlbums = MutableStateFlow<List<AlbumDisplay>>(emptyList())
    val dailyAlbums: StateFlow<List<AlbumDisplay>> = _dailyAlbums

    private val _featuredTracks = MutableStateFlow<List<Track>>(emptyList())
    val featuredTracks: StateFlow<List<Track>> = _featuredTracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recent = repo.searchTracks("recent")
                val daily  = repo.searchTracks("daily mix")
                _recentAlbums.value = groupByUser(recent)
                _dailyAlbums.value  = groupByUser(daily)
                _featuredTracks.value = repo.searchTracks("electronic")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun groupByUser(tracks: List<Track>): List<AlbumDisplay> =
        tracks.groupBy { it.user.id to it.user.name }
            .map { (key, list) ->
                val (userId, userName) = key
                AlbumDisplay(
                    userId   = userId,
                    title    = userName,
                    coverUrl = list.firstOrNull()?.artwork?.`150x150`
                )
            }
}
