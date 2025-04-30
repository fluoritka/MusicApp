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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.AlbumDisplay
import com.example.musicapp.model.Track
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onGoToPlayer: (Track) -> Unit,
    onAlbumClick: (String) -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val userId by authViewModel.currentUserId.collectAsState()
    val recent   by homeViewModel.recentAlbums.collectAsState()
    val daily    by homeViewModel.dailyAlbums.collectAsState()
    val featured by homeViewModel.featuredTracks.collectAsState()
    val loading  by homeViewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        userId?.let { homeViewModel.loadHomeData(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Hello, welcome back!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AlbumSection("Recently Played", recent, onAlbumClick)
            AlbumSection("Daily Mix",       daily,  onAlbumClick)
            TrackSection("Today’s Picks",   featured, onGoToPlayer)
        }
    }
}

@Composable
private fun AlbumSection(
    title: String,
    albums: List<AlbumDisplay>,
    onAlbumClick: (String) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(albums) { album ->
            Column(
                Modifier
                    .width(140.dp)
                    .clickable { onAlbumClick(album.userId) }
            ) {
                Card(
                    Modifier
                        .size(140.dp)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = album.coverUrl,
                        contentDescription = album.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(album.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1)
            }
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun TrackSection(
    title: String,
    tracks: List<Track>,
    onItemClick: (Track) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(tracks) { track ->
            Card(
                Modifier
                    .size(140.dp)
                    .clickable { onItemClick(track) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    model = track.artwork?.`150x150`,
                    contentDescription = track.title,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    Spacer(Modifier.height(24.dp))
}
