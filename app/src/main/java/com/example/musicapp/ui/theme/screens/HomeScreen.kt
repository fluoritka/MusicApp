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
import com.example.musicapp.model.*
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onPlayTrack : (Track)  -> Unit,
    onAlbumClick: (String) -> Unit,
    authVm:  AuthViewModel  = viewModel(),
    homeVm:  HomeViewModel  = viewModel()
) {
    /* -------- state -------- */
    val userId   by authVm.currentUserId.collectAsState()
    val recent   by homeVm.recentTracks.collectAsState()
    val mixes    by homeVm.dailyAlbums.collectAsState()
    val featured by homeVm.featuredTracks.collectAsState()
    val loading  by homeVm.isLoading.collectAsState()

    LaunchedEffect(userId) { userId?.let(homeVm::loadHomeData) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Hello, welcome back!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(24.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            return
        }

        /* ---------- RECENTLY PLAYED ---------- */
        Text("Recently Played", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        SavedTrackRow(
            tracks = recent,
            onTrackClick = { st ->
                /* конвертация SavedTrack → Track под вашу модель */
                val art = Artwork(
                    `150x150`   = st.imageUrl,
                    `480x480`   = st.imageUrl,
                    `1000x1000` = st.imageUrl
                )
                val usr = User(st.trackUserId, st.artist ?: "")
                val t   = Track(id = st.id, title = st.title ?: "", user = usr, artwork = art)
                onPlayTrack(t)
            }
        )
        Spacer(Modifier.height(24.dp))

        /* ---------- DAILY MIX ---------- */
        AlbumSection("Daily Mix", mixes, onAlbumClick)
        Spacer(Modifier.height(24.dp))

        /* ---------- TODAY’S PICKS ---------- */
        TrackSection("Today’s Picks", featured, onPlayTrack)
    }
}

/* ==================================================================== */
/* ---------------------------- helpers -------------------------------- */

@Composable
private fun SavedTrackRow(
    tracks: List<SavedTrack>,
    onTrackClick: (SavedTrack) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(tracks) { st ->
            Column(
                Modifier
                    .width(140.dp)
                    .clickable { onTrackClick(st) }
            ) {
                Card(
                    Modifier.size(140.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = st.imageUrl,
                        contentDescription = st.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    st.title ?: "",
                    style    = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AlbumSection(
    title: String,
    albums: List<AlbumDisplay>,
    onAlbumClick: (String) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(albums) { album ->
            Column(
                Modifier
                    .width(140.dp)
                    .clickable { onAlbumClick(album.userId) }
            ) {
                Card(
                    Modifier.size(140.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
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
                    maxLines = 1)
            }
        }
    }
}

@Composable
private fun TrackSection(
    title: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(tracks) { track ->
            Card(
                Modifier
                    .size(140.dp)
                    .clickable { onTrackClick(track) },
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    model = track.artwork?.`150x150`,
                    contentDescription = track.title,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
