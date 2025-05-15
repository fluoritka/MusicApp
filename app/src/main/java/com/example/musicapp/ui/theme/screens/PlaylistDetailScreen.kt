// PlaylistDetailScreen.kt
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.Artwork
import com.example.musicapp.model.Track
import com.example.musicapp.model.User
import com.example.musicapp.viewmodel.PlaylistViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: String,
    onBack: () -> Unit,
    onPlayerClick: () -> Unit,
    playlistVm: PlaylistViewModel = viewModel(),
    playerVm: PlayerViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)
) {
    val playlists    by playlistVm.playlists.collectAsState()
    val pl           = playlists.firstOrNull { it.id == playlistId }

    // Состояние общего плеера
    val currentTrack by playerVm.currentTrack.collectAsState()
    val isPlaying    by playerVm.isPlaying.collectAsState()
    val progress     by playerVm.progress.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(pl?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        val tr = Track(
                            id      = saved.id.orEmpty(),
                            title   = saved.title.orEmpty(),
                            user    = User(
                                id   = saved.trackUserId.orEmpty(),
                                name = saved.artist.orEmpty()
                            ),
                            artwork = Artwork(
                                `150x150`   = saved.imageUrl.orEmpty(),
                                `480x480`   = saved.imageUrl.orEmpty(),
                                `1000x1000` = saved.imageUrl.orEmpty()
                            )
                        )
                        // Безопасно получаем URL обложки
                        val imageUrl = tr.artwork?.`150x150`.orEmpty()

                        ListItem(
                            leadingContent = {
                                AsyncImage(
                                    model = imageUrl,
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

            // Мини-плеер внизу
            currentTrack?.let { track ->

            }
        }
    }
}
