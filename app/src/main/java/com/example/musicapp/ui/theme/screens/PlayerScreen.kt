package com.example.musicapp.ui.theme.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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

    val playlists by playlistVm.playlists.collectAsState()
    var showPlaylistDialog by remember { mutableStateOf(false) }

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

                    Slider(
                        value = progress,
                        onValueChange = playerVm::seekTo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Кнопка выбора плейлиста
                        IconButton(
                            onClick = { if (track != null) showPlaylistDialog = true },
                            enabled = track != null && playlists.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Add to playlist"
                            )
                        }

                        // Тоггл избранного
                        val favorites by playlistVm.favorites.collectAsState()
                        val isFav = track?.id?.let { id -> favorites.any { it.trackId == id } } == true
                        IconButton(onClick = { track?.let { playlistVm.toggleFavorite(it) } }) {
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

                        FilledIconButton(onClick = playerVm::toggle, modifier = Modifier.size(72.dp)) {
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

    // Диалог выбора плейлиста
    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Выберите плейлист") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    playlists.forEach { pl ->
                        TextButton(
                            onClick = {
                                track?.let { playlistVm.addTrackToPlaylist(it, pl.id) }
                                showPlaylistDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(pl.title)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPlaylistDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
