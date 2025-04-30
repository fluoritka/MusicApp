package com.example.musicapp.ui.theme.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* --- тёмная схема под Spotify-green --- */
private val DarkSpotifyScheme = darkColorScheme(
    primary       = SpotifyGreen,
    onPrimary     = Color.White,

    background    = BlackBackground,
    onBackground  = Color.White,

    surface       = BlackSurface,
    onSurface     = GrayOnSurface,

    secondary     = SpotifyGreen,
    onSecondary   = Color.White,
)

@Composable
fun MusicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkSpotifyScheme,
        typography  = Typography,
        content     = content
    )
}
