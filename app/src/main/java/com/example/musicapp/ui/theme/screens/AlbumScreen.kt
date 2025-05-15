// app/src/main/java/com/example/musicapp/ui/theme/screens/AlbumScreen.kt
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import com.example.musicapp.network.RetrofitInstance
import com.example.musicapp.repository.AudiusRepository
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun AlbumScreen(
    userId: String,
    onTrackClick: () -> Unit
) {
    val owner = LocalContext.current as ViewModelStoreOwner

    val authVm: AuthViewModel     = viewModel(owner)
    val homeVm: HomeViewModel     = viewModel(owner)
    val playerVm: PlayerViewModel = viewModel(owner)

    if (userId == "recent") {
        val currentUserId by authVm.currentUserId.collectAsState()
        val recentTracks  by homeVm.recentTracks.collectAsState()

        LaunchedEffect(currentUserId) {
            currentUserId?.let { homeVm.loadHomeData(it) }
        }

        val uniqueTracks = remember(recentTracks) {
            recentTracks.distinctBy { it.title }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Recently Played", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uniqueTracks) { st ->
                    ListItem(
                        headlineContent   = { Text(st.title.orEmpty()) },
                        supportingContent = { Text(st.artist.orEmpty()) },
                        leadingContent    = {
                            AsyncImage(
                                model             = st.imageUrl,
                                contentDescription = st.title,
                                modifier          = Modifier.size(48.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                playerVm.playSaved(st, uniqueTracks)
                                onTrackClick()
                            }
                    )
                }
            }
        }
    } else {
        val repo = remember { AudiusRepository(RetrofitInstance.api) }
        var tracks  by remember { mutableStateOf<List<Track>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(userId) {
            loading = true
            tracks  = runCatching { repo.getUserTracks(userId) }.getOrDefault(emptyList())
            loading = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tracks) { track ->
                        ListItem(
                            headlineContent   = { Text(track.title) },
                            supportingContent = { Text(track.user.name) },
                            leadingContent    = {
                                AsyncImage(
                                    model             = track.artwork?.`150x150`,
                                    contentDescription = track.title,
                                    modifier          = Modifier.size(48.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playerVm.play(track, tracks)
                                    onTrackClick()
                                }
                        )
                    }
                }
            }
        }
    }
}
