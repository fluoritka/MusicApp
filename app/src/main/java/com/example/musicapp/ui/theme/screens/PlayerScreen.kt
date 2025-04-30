package com.example.musicapp.ui.theme.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)

    val track    by playerVm.currentTrack.collectAsState()
    val playing  by playerVm.isPlaying.collectAsState()
    val progress by playerVm.progress.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(track?.title ?: "Now Playing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center
        ) {
            if (track == null) {
                Text("Ничего не играет")
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
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

                    Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                        IconButton(onClick = { /* prev */ }, enabled = false) {
                            Icon(Icons.Default.SkipPrevious, null)
                        }
                        FilledIconButton(onClick = playerVm::toggle, Modifier.size(72.dp)) {
                            Icon(
                                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                null,
                                Modifier.size(36.dp)
                            )
                        }
                        IconButton(onClick = { /* next */ }, enabled = false) {
                            Icon(Icons.Default.SkipNext, null)
                        }
                    }
                }
            }
        }
    }
}
