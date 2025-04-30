package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicapp.model.Track

/**
 * Мини-плеер а-ля Spotify: занимает всю ширину снизу экрана,
 * сверху тонкая progress-bar, далее строка с обложкой, тайтлом
 * и базовыми контролами.
 */
@Composable
fun ModernMiniPlayerBar(
    track: Track,
    isPlaying: Boolean,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onSkipPrevious: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onPlayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPlayerClick() }
    ) {
        Column {
            /* --- тонкий индикатор прогресса сверху --- */
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color     = MaterialTheme.colorScheme.primary,
                trackColor= MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )

            /* --- содержимое мини-плеера --- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                AsyncImage(
                    model = track.artwork?.`150x150`,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text  = track.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
                    Text(
                        text  = track.user.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onSkipPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Prev")
                }
                FilledIconButton(
                    onClick = onPlayPauseToggle,
                    shape   = RoundedCornerShape(50)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onSkipNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                }
            }
        }
    }
}
