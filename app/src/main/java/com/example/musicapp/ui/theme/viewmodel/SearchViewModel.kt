// app/src/main/java/com/example/musicapp/viewmodel/SearchViewModel.kt
package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val repository = AudiusRepository(RetrofitInstance.api)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    init {
        viewModelScope.launch {
            _query
                .debounce(300)                      // ждём 300 мс после остановки ввода
                .distinctUntilChanged()            // только если изменился
                .filter { it.isNotBlank() }        // пропускаем пустые
                .onEach { _isSearching.value = true }
                .flatMapLatest { q ->
                    flow {
                        val result = withContext(Dispatchers.IO) {
                            repository.searchTracks(q)
                        }
                        emit(result)
                    }.catch { emit(emptyList()) }
                }
                .onEach { list ->
                    _tracks.value = list
                    _isSearching.value = false
                }
                .launchIn(this)
        }
    }

    fun onQueryChange(new: String) {
        _query.value = new
    }
}
