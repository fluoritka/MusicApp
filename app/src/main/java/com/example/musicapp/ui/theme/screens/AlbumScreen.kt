package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import coil.compose.AsyncImage
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import kotlinx.coroutines.launch

@Composable
fun AlbumScreen(
    userId: String,
    onTrackClick: (Track) -> Unit
) {
    val repo = remember { AudiusRepository(RetrofitInstance.api) }
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        loading = true
        scope.launch {
            tracks = try {
                repo.getUserTracks(userId)
            } catch (_: Exception) {
                emptyList()
            }
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tracks) { track ->
                    ListItem(
                        headlineContent   = { Text(track.title) },
                        supportingContent = { Text(track.user.name) },
                        leadingContent    = {
                            AsyncImage(
                                model = track.artwork?.`150x150`,
                                contentDescription = track.title,
                                modifier = Modifier.size(48.dp)
                            )
                        },
                        modifier = Modifier.clickable { onTrackClick(track) }
                    )
                }
            }
        }
    }
}
