package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = AudiusRepository(RetrofitInstance.api)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchJob: Job? = null

    fun search(query: String) {
        // Отменяем предыдущий запрос, если пользователь продолжает печатать
        searchJob?.cancel()

        if (query.isBlank()) {
            _tracks.value = emptyList()
            return
        }

        // Делаем небольшую задержку, чтобы не дергать API на каждый символ
        searchJob = viewModelScope.launch {
            delay(300)
            try {
                _isSearching.value = true
                _tracks.value = repository.searchTracks(query)
            } catch (e: Exception) {
                e.printStackTrace()
                // В реальном приложении лучше показывать ошибку пользователю
            } finally {
                _isSearching.value = false
            }
        }
    }
}
