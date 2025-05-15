package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val repository = AudiusRepository(RetrofitInstance.api)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchJob: Job? = null

    fun search(query: String) {
        // отменяем предыдущий запрос при быстром вводе
        searchJob?.cancel()

        if (query.isBlank()) {
            _tracks.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            // дебаунс 300 мс
            delay(300)
            _isSearching.value = true
            try {
                // сетевой вызов на IO
                val result = withContext(Dispatchers.IO) {
                    repository.searchTracks(query)
                }
                _tracks.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }
}
