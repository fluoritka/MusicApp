// Экран главного раздела: приветствие и секции альбомов (Recently Played, Daily Mix, Today's Picks)
@file:Suppress("UnusedImport")
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.model.AlbumDisplay
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    authVm: AuthViewModel = viewModel(),    // ViewModel для аутентификации
    homeVm: HomeViewModel = viewModel()     // ViewModel для данных главного экрана
) {
    // Состояния: текущий пользователь и данные секций
    val userId          by authVm.currentUserId.collectAsState()
    val recentAlbums    by homeVm.recentAlbums.collectAsState()
    val mixes           by homeVm.dailyAlbums.collectAsState()
    val recommendations by homeVm.recommendations.collectAsState()
    val loading         by homeVm.isLoading.collectAsState()

    // Плеер для воспроизведения треков
    val playerVm: PlayerViewModel = viewModel()

    // Загрузка данных при входе пользователя
    LaunchedEffect(userId) {
        userId?.let { homeVm.loadHomeData(it) }
    }

    // Показываем индикатор загрузки, пока данные не пришли
    if (loading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Основная разметка: заголовок и три секции альбомов
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text  = "Hello, welcome back!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(24.dp))

        // Секция «Recently Played»
        AlbumSection(
            title        = "Recently Played",
            albums       = recentAlbums,
            onAlbumClick = { uid -> navController.navigate("album/$uid") }
        )
        Spacer(Modifier.height(24.dp))

        // Секция «Daily Mix»
        AlbumSection(
            title        = "Daily Mix",
            albums       = mixes,
            onAlbumClick = { uid -> navController.navigate("album/$uid") }
        )
        Spacer(Modifier.height(24.dp))

        // Секция «Today's Picks»
        AlbumSection(
            title        = "Today's Picks",
            albums       = recommendations,
            onAlbumClick = { uid -> navController.navigate("album/$uid") }
        )
    }
}

@Composable
private fun AlbumSection(
    title: String,
    albums: List<AlbumDisplay>,
    onAlbumClick: (String) -> Unit
) {
    // Заголовок секции
    Text(text = title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(albums) { alb ->
            Column(
                Modifier
                    .width(140.dp)
                    .clickable { onAlbumClick(alb.userId) } // навигация по клику
            ) {
                Card(
                    modifier = Modifier.size(140.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model           = alb.coverUrl,           // обложка альбома
                        contentDescription = alb.title,
                        contentScale    = ContentScale.Crop,
                        modifier        = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = alb.title,
                    style    = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}