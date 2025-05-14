package com.example.musicapp.ui.theme.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.Track
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)
    val playlistVm: PlaylistViewModel = viewModel()

    val track    by playerVm.currentTrack.collectAsState()
    val playing  by playerVm.isPlaying.collectAsState()
    val progress by playerVm.progress.collectAsState()

    // for default add-to-playlist, take first playlist
    val playlists by playlistVm.playlists.collectAsState()
    val defaultPlId = playlists.firstOrNull()?.id

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(track?.title ?: "Now Playing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (track == null) {
                Text("Ничего не играет")
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    AsyncImage(
                        model = track!!.artwork?.`480x480` ?: track!!.artwork?.`150x150`,
                        contentDescription = track!!.title,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Text(track!!.title, style = MaterialTheme.typography.headlineSmall)
                    Text(track!!.user.name, style = MaterialTheme.typography.bodyMedium)

                    /** progress – Float in 0f..1f */
                    Slider(
                        value = progress,
                        onValueChange = playerVm::seekTo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Add to playlist button
                        IconButton(
                            onClick = {
                                defaultPlId?.let { playlistVm.addTrackToPlaylist(track!!, it) }
                            },
                            enabled = defaultPlId != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Add to playlist"
                            )
                        }

                        // Favorite toggle button
                        val favorites by playlistVm.favorites.collectAsState()
                        val isFav = track?.id?.let { id -> favorites.any { it.trackId == id } } == true
                        IconButton(
                            onClick = { playlistVm.toggleFavorite(track!!) }
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle favorite"
                            )
                        }

                        IconButton(
                            onClick = playerVm::skipPrevious,
                            enabled = playerVm.index.collectAsState().value > 0
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                        }

                        FilledIconButton(
                            onClick = playerVm::toggle,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                null,
                                Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = playerVm::skipNext,
                            enabled = playerVm.index.collectAsState().value < playerVm.queue.collectAsState().value.lastIndex
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}
