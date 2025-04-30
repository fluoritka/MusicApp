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
import com.example.musicapp.model.SavedTrack
import com.example.musicapp.model.Track
import com.example.musicapp.viewmodel.SearchViewModel
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import com.example.musicapp.model.RealmUser
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val tracks by viewModel.tracks.collectAsState()

    var activeIndex by remember { mutableStateOf<Int?>(null) }
    var isPlaying   by remember { mutableStateOf(false) }
    var progress    by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val exo = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(exo) { onDispose { exo.release() } }

    // Realm для сохранения прослушиваний
    val realm = remember {
        val config = RealmConfiguration.Builder(
            schema = setOf<KClass<out TypedRealmObject>>(RealmUser::class, SavedTrack::class)
        )
            .name("musicapp.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.open(config)
    }

    val currentUserId by authViewModel.currentUserId.collectAsState()
    val scope = rememberCoroutineScope()                // ← CoroutineScope

    LaunchedEffect(activeIndex, exo) {
        while(true) {
            val dur = exo.duration
            progress = if (dur > 0) exo.currentPosition / dur.toFloat() else 0f
            kotlinx.coroutines.delay(500)
        }
    }

    val focus = LocalFocusManager.current

    Column(Modifier.fillMaxSize()) {
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
                            // play/pause
                            if (activeIndex == idx) {
                                if (isPlaying) exo.pause().also { isPlaying = false }
                                else          exo.play().also  { isPlaying = true }
                            } else {
                                exo.stop(); exo.clearMediaItems()
                                exo.setMediaItem(MediaItem.fromUri(track.streamUrl))
                                exo.prepare(); exo.play()
                                activeIndex = idx; isPlaying = true
                            }
                            // сохраняем в Realm внутри coroutine
                            currentUserId?.let { uid ->
                                scope.launch {
                                    realm.write {
                                        copyToRealm(SavedTrack().apply {
                                            id             = track.id
                                            title          = track.title
                                            artist         = track.user.name
                                            imageUrl       = track.artwork?.`150x150`
                                            userId         = uid
                                            trackUserId    = track.user.id
                                            playedAt       = System.currentTimeMillis()
                                        })
                                    }
                                }
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
                            if (activeIndex == idx) {
                                if (isPlaying) exo.pause().also { isPlaying = false }
                                else          exo.play().also  { isPlaying = true }
                            } else {
                                exo.stop(); exo.clearMediaItems()
                                exo.setMediaItem(MediaItem.fromUri(track.streamUrl))
                                exo.prepare(); exo.play()
                                activeIndex = idx; isPlaying = true
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

        activeIndex?.takeIf { it in tracks.indices }?.let { idx ->
            val track = tracks[idx]
            ModernMiniPlayerBar(
                track             = track,
                isPlaying         = isPlaying,
                progress          = progress,
                onProgressChange  = { newProg -> exo.seekTo((exo.duration * newProg).toLong()) },
                onSkipPrevious    = { /* ... */ },
                onPlayPauseToggle = { /* ... */ },
                onSkipNext        = { /* ... */ },
                onPlayerClick     = { /* ... */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
