package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.Artwork
import com.example.musicapp.model.Track
import com.example.musicapp.model.User
import com.example.musicapp.ui.theme.screens.ModernMiniPlayerBar
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: String,
    onBack: () -> Unit,
    onPlayerClick: () -> Unit,
    playlistVm: PlaylistViewModel = viewModel(),
    playerVm:   PlayerViewModel   = viewModel()
) {
    val playlists by playlistVm.playlists.collectAsState()
    val pl        = playlists.firstOrNull { it.id == playlistId }

    // Текущее состояние плеера
    val currentTrack by playerVm.currentTrack.collectAsState()
    val isPlaying    by playerVm.isPlaying.collectAsState()
    val progress     by playerVm.progress.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = pl?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            currentTrack?.let { track ->
                ModernMiniPlayerBar(
                    track = track,
                    isPlaying = isPlaying,
                    progress = progress,
                    onProgressChange = playerVm::seekTo,
                    onSkipPrevious = playerVm::skipPrevious,
                    onPlayPauseToggle = playerVm::toggle,
                    onSkipNext = playerVm::skipNext,
                    onPlayerClick = onPlayerClick
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (pl == null) {
                Text("Плейлист не найден", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pl.tracks) { saved ->
                        val art = Artwork(
                            `150x150`   = saved.imageUrl.orEmpty(),
                            `480x480`   = saved.imageUrl.orEmpty(),
                            `1000x1000` = saved.imageUrl.orEmpty()
                        )
                        val usr = User(
                            id   = saved.trackUserId.orEmpty(),
                            name = saved.artist.orEmpty()
                        )
                        val tr = Track(
                            id      = saved.id.orEmpty(),
                            title   = saved.title.orEmpty(),
                            user    = usr,
                            artwork = art
                        )

                        ListItem(
                            leadingContent = {
                                AsyncImage(
                                    model = tr.artwork?.`150x150`.orEmpty(),
                                    contentDescription = tr.title,
                                    modifier = Modifier.size(56.dp)
                                )
                            },
                            headlineContent   = { Text(tr.title) },
                            supportingContent = { Text(tr.user.name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val queue = pl.tracks.map { s ->
                                        Track(
                                            id      = s.id.orEmpty(),
                                            title   = s.title.orEmpty(),
                                            user    = User(
                                                id   = s.trackUserId.orEmpty(),
                                                name = s.artist.orEmpty()
                                            ),
                                            artwork = Artwork(
                                                `150x150`   = s.imageUrl.orEmpty(),
                                                `480x480`   = s.imageUrl.orEmpty(),
                                                `1000x1000` = s.imageUrl.orEmpty()
                                            )
                                        )
                                    }
                                    playerVm.play(tr, queue)
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
