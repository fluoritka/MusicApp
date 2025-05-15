// app/src/main/java/com/example/musicapp/ui/theme/screens/SearchScreen.kt
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.model.Track
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchVm: SearchViewModel = viewModel()
) {
    // Привязываем PlayerViewModel к тому же ViewModelStoreOwner, что и в MainActivity/NavHost
    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)

    var query by remember { mutableStateOf("") }
    val tracks      by searchVm.tracks.collectAsState()
    val isSearching by searchVm.isSearching.collectAsState()
    val playing     by playerVm.isPlaying.collectAsState()
    val current     by playerVm.currentTrack.collectAsState()
    val focus       = LocalFocusManager.current

    // Автопоиск при каждом изменении query
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            searchVm.onQueryChange(query)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value            = query,
            onValueChange    = { query = it },
            label            = { Text("Search…") },
            singleLine       = true,
            modifier         = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            keyboardOptions  = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions  = KeyboardActions(onSearch = {
                searchVm.onQueryChange(query)
                focus.clearFocus()
            })
        )

        if (isSearching) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        LazyColumn(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(tracks) { _, track ->
                TrackRow(
                    track    = track,
                    isNow    = current?.id == track.id,
                    playing  = playing,
                    onClick  = { playerVm.play(track, tracks) }
                )
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    isNow: Boolean,
    playing: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model              = track.artwork?.`150x150`,
                contentDescription = track.title,
                modifier           = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text     = track.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text     = track.user.name,
                    style    = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector        = if (isNow && playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null
            )
        }
    }
}
