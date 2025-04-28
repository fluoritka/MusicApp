package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicapp.model.Track
import com.example.musicapp.viewmodel.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    var query by remember { mutableStateOf("") }
    val tracks by viewModel.tracks.collectAsState()

    var activeIndex by remember { mutableStateOf<Int?>(null) }
    var isPlaying   by remember { mutableStateOf(false) }
    var progress    by remember { mutableStateOf(0f) }

    // ExoPlayer
    val context = LocalContext.current
    val exo = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(exo) { onDispose { exo.release() } }

    // Обновляем прогресс
    LaunchedEffect(activeIndex, exo) {
        while(true) {
            val dur = exo.duration
            progress = if (dur > 0) exo.currentPosition / dur.toFloat() else 0f
            delay(500)
        }
    }

    val focus = LocalFocusManager.current

    Column(Modifier.fillMaxSize()) {
        // Поисковая строка
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search…") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.search(query)
                focus.clearFocus()
            })
        )

        // Список треков
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(tracks) { idx, track ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Запускаем/ставим на паузу или меняем трек
                            if (activeIndex == idx) {
                                if (isPlaying) exo.pause().also { isPlaying = false }
                                else          exo.play().also  { isPlaying = true }
                            } else {
                                exo.stop()
                                exo.clearMediaItems()
                                exo.setMediaItem(MediaItem.fromUri(track.streamUrl))
                                exo.prepare()
                                exo.play()
                                activeIndex = idx
                                isPlaying   = true
                            }
                        }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = track.artwork?.`150x150`,
                            contentDescription = track.title,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium)
                            Text(track.user.name, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = {
                            // Повторно та же логика клика по карточке
                            if (activeIndex == idx) {
                                if (isPlaying) exo.pause().also { isPlaying = false }
                                else          exo.play().also  { isPlaying = true }
                            } else {
                                exo.stop()
                                exo.clearMediaItems()
                                exo.setMediaItem(MediaItem.fromUri(track.streamUrl))
                                exo.prepare()
                                exo.play()
                                activeIndex = idx
                                isPlaying   = true
                            }
                        }) {
                            Icon(
                                imageVector = if (activeIndex == idx && isPlaying)
                                    Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        // Мини‑плеер
        activeIndex?.takeIf { it in tracks.indices }?.let { idx ->
            val track = tracks[idx]
            ModernMiniPlayerBar(
                track             = track,
                isPlaying         = isPlaying,
                progress          = progress,
                onProgressChange  = { newProg ->
                    exo.seekTo((exo.duration * newProg).toLong())
                },
                onSkipPrevious    = {
                    if (idx > 0) {
                        // переключаем на предыдущий
                        val prev = tracks[idx - 1]
                        exo.stop(); exo.clearMediaItems()
                        exo.setMediaItem(MediaItem.fromUri(prev.streamUrl))
                        exo.prepare(); exo.play()
                        activeIndex = idx - 1
                        isPlaying   = true
                    }
                },
                onPlayPauseToggle = {
                    if (isPlaying) exo.pause().also { isPlaying = false }
                    else          exo.play().also  { isPlaying = true }
                },
                onSkipNext        = {
                    if (idx < tracks.lastIndex) {
                        val next = tracks[idx + 1]
                        exo.stop(); exo.clearMediaItems()
                        exo.setMediaItem(MediaItem.fromUri(next.streamUrl))
                        exo.prepare(); exo.play()
                        activeIndex = idx + 1
                        isPlaying   = true
                    }
                },
                onPlayerClick     = {
                    // TODO: навигация на полноэкранный плеер
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
