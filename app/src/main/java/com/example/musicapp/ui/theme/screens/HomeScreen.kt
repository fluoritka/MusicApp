// app/src/main/java/com/example/musicapp/ui/theme/screens/HomeScreen.kt
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
import coil.compose.AsyncImage
import com.example.musicapp.model.*
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onPlayTrack: (Track) -> Unit,
    onAlbumClick: (String) -> Unit,
    authVm: AuthViewModel = viewModel(),
    homeVm: HomeViewModel = viewModel()
) {
    val userId          by authVm.currentUserId.collectAsState()
    val recent          by homeVm.recentTracks.collectAsState()
    val mixes           by homeVm.dailyAlbums.collectAsState()
    val recommendations by homeVm.recommendations.collectAsState()
    val loading         by homeVm.isLoading.collectAsState()

    // Подгружаем данные при каждом показе экрана
    LaunchedEffect(Unit) {
        userId?.let { homeVm.loadHomeData(it) }
    }

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

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Hello, welcome back!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(24.dp))

        Text(text = "Recently Played", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recent) { st ->
                Column(
                    Modifier
                        .width(140.dp)
                        .clickable {
                            val art = Artwork(
                                `150x150`   = st.imageUrl,
                                `480x480`   = st.imageUrl,
                                `1000x1000` = st.imageUrl
                            )
                            val usr = User(st.trackUserId, st.artist.orEmpty())
                            val t = Track(
                                id = st.id,
                                title = st.title.orEmpty(),
                                user = usr,
                                artwork = art
                            )
                            onPlayTrack(t)
                        }
                ) {
                    Card(
                        modifier = Modifier.size(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        AsyncImage(
                            model = st.imageUrl,
                            contentDescription = st.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = st.title.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        // Daily Mix
        AlbumSection(
            title = "Daily Mix",
            albums = mixes,
            onAlbumClick = onAlbumClick
        )

        Spacer(Modifier.height(24.dp))
        // Today's Picks
        AlbumSection(
            title = "Today's Picks",
            albums = recommendations,
            onAlbumClick = onAlbumClick
        )
    }
}

@Composable
private fun AlbumSection(
    title: String,
    albums: List<AlbumDisplay>,
    onAlbumClick: (String) -> Unit
) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(albums) { alb ->
            Column(
                Modifier
                    .width(140.dp)
                    .clickable { onAlbumClick(alb.userId) }
            ) {
                Card(
                    modifier = Modifier.size(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = alb.coverUrl,
                        contentDescription = alb.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = alb.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}
