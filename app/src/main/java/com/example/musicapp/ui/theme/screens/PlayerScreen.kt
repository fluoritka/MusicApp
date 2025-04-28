package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Now Playing", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        // Тут можно вывести обложку, таймер, слайдер и кнопки play/pause
        Text("Artist – Track Title", style = MaterialTheme.typography.bodyLarge)
    }
}
