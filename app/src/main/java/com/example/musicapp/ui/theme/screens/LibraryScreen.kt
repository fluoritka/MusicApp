// Экран «Библиотека»: отображает избранные треки и пользовательские плейлисты
@file:Suppress("UnusedImport")
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.musicapp.model.Artwork
import com.example.musicapp.model.Track
import com.example.musicapp.model.User
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.PlaylistViewModel

@Composable
fun LibraryScreen(
    onOpenPlaylist: (String) -> Unit,             // навигация при открытии плейлиста
    playlistVm: PlaylistViewModel = viewModel()    // ViewModel для работы с плейлистами
) {
    // Состояния списка плейлистов и избранных треков
    val playlists by playlistVm.playlists.collectAsState()
    val favorites by playlistVm.favorites.collectAsState()
    // ViewModel плеера для воспроизведения треков
    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)

    // Показ диалога создания нового плейлиста
    var showDlg by remember { mutableStateOf(false) }
    var title   by remember { mutableStateOf("") }

    if (showDlg) {
        // Диалог ввода названия нового плейлиста
        AlertDialog(
            onDismissRequest = { showDlg = false },
            title            = { Text("Новый плейлист") },
            text             = {
                OutlinedTextField(
                    value        = title,
                    onValueChange = { title = it },
                    label        = { Text("Название") }
                )
            },
            confirmButton   = {
                TextButton(onClick = {
                    if (title.isNotBlank()) playlistVm.createPlaylist(title.trim())
                    title = ""; showDlg = false
                }) { Text("Создать") }
            },
            dismissButton   = {
                TextButton(onClick = { showDlg = false }) { Text("Отмена") }
            }
        )
    }

    // Основная вертикальная прокрутка: избранное и плейлисты
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Раздел избранных треков
        item {
            Text("Любимые треки", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { ft ->
                    Column(
                        Modifier
                            .width(140.dp)
                            .clickable {
                                // Преобразуем SavedTrack в Track и воспроизводим
                                val tr = Track(
                                    id      = ft.trackId,
                                    title   = ft.title,
                                    user    = User(ft.userId, ft.artist),
                                    artwork = Artwork(
                                        `150x150`   = ft.imageUrl,
                                        `480x480`   = ft.imageUrl,
                                        `1000x1000` = ft.imageUrl
                                    )
                                )
                                playerVm.play(tr, listOf(tr))
                            }
                    ) {
                        Card(Modifier.size(140.dp)) {
                            AsyncImage(
                                model              = ft.imageUrl,
                                contentDescription = ft.title,
                                modifier           = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(ft.title, maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Заголовок и кнопка создания нового плейлиста
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Плейлисты",
                    style    = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                TextButton(onClick = { showDlg = true }) {
                    Text("Создать")
                }
            }
        }

        // Список пользовательских плейлистов
        items(playlists) { pl ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPlaylist(pl.id) } // открываем выбранный плейлист
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(pl.title, style = MaterialTheme.typography.titleMedium)
                    Text("${pl.tracks.size} трек(ов)",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}