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
fun PlayerScreen(onBack: () -> Unit) { // Компонент экрана плеера
    BackHandler { onBack() } // Обработка нажатия "назад"

    // Получаем ViewModel для управления воспроизведением
    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)
    // Получаем ViewModel для работы с плейлистами и избранным
    val playlistVm: PlaylistViewModel = viewModel()

    // Подписываемся на состояние текущего трека
    val track    by playerVm.currentTrack.collectAsState()
    // Подписываемся на состояние воспроизведения (играет/пауза)
    val playing  by playerVm.isPlaying.collectAsState()
    // Подписываемся на прогресс воспроизведения
    val progress by playerVm.progress.collectAsState()

    // Подписываемся на список плейлистов
    val playlists by playlistVm.playlists.collectAsState()
    // Флаг для показа диалога выбора плейлиста
    var showPlaylistDialog by remember { mutableStateOf(false) }

    Scaffold( // Основная структура экрана
        topBar = {
            SmallTopAppBar( // Верхняя панель с кнопкой "назад" и заголовком
                title = { Text(track?.title ?: "Now Playing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box( // Контейнер для центрирования содержимого
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (track == null) { // Если трек не загружен
                Text("Ничего не играет")
            } else {
                Column( // Вертикальный список элементов плеера
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    AsyncImage( // Отображаем обложку трека
                        model = track!!.artwork?.`480x480` ?: track!!.artwork?.`150x150`,
                        contentDescription = track!!.title,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Text(track!!.title, style = MaterialTheme.typography.headlineSmall) // Название трека
                    Text(track!!.user.name, style = MaterialTheme.typography.bodyMedium) // Имя исполнителя

                    Slider( // Ползунок прогресса воспроизведения
                        value = progress,
                        onValueChange = playerVm::seekTo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row( // Контейнер для кнопок управления
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Кнопка добавления в плейлист
                        IconButton(
                            onClick = { if (track != null) showPlaylistDialog = true },
                            enabled = track != null && playlists.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Add to playlist"
                            )
                        }

                        // Получаем список избранных треков
                        val favorites by playlistVm.favorites.collectAsState()
                        // Проверяем, находится ли текущий трек в избранном
                        val isFav = track?.id?.let { id -> favorites.any { it.trackId == id } } == true
                        // Кнопка "избранное"
                        IconButton(onClick = { track?.let { playlistVm.toggleFavorite(it) } }) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle favorite"
                            )
                        }

                        // Кнопка перехода к предыдущему треку
                        IconButton(
                            onClick = playerVm::skipPrevious,
                            enabled = playerVm.index.collectAsState().value > 0
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                        }

                        // Кнопка play/pause
                        FilledIconButton(onClick = playerVm::toggle, modifier = Modifier.size(72.dp)) {
                            Icon(
                                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                null,
                                Modifier.size(36.dp)
                            )
                        }

                        // Кнопка перехода к следующему треку
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
                Column( // Список плейлистов внутри диалога
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    playlists.forEach { pl -> // Для каждого плейлиста создаём кнопку
                        TextButton(
                            onClick = {
                                track?.let { playlistVm.addTrackToPlaylist(it, pl.id) } // Добавляем трек в выбранный плейлист
                                showPlaylistDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(pl.title) // Название плейлиста
                        }
                    }
                }
            },
            confirmButton = {}, // без кнопки подтверждения
            dismissButton = {
                TextButton(onClick = { showPlaylistDialog = false }) { // Кнопка отмены
                    Text("Отмена")
                }
            }
        )
    }
}
