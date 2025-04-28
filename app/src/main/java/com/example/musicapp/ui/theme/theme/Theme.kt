// app/src/main/java/com/example/musicapp/ui/theme/theme/Theme.kt
package com.example.musicapp.ui.theme.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Основная тёмная палитра в чёрно‑фиолетовых тонах
private val BlackPurpleColors = darkColorScheme(
    primary      = Color(0xFF8E24AA), // фиолетовый акцент
    onPrimary    = Color.White,

    background   = Color(0xFF121212), // почти чёрный фон
    onBackground = Color.White,

    surface      = Color(0xFF1E1E1E), // тёмно‑серые карточки
    onSurface    = Color(0xFFB0BEC5), // серый текст на поверхности

    secondary    = Color(0xFF5E35B1), // тёмный фиолетовый вариант
    onSecondary  = Color.White,
)

@Composable
fun MusicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlackPurpleColors,
        typography  = Typography,
        content     = content
    )
}
