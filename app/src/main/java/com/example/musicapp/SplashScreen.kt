package com.example.musicapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    Surface(color = Color.Black, modifier = Modifier.padding(16.dp)) {
        Text(text = "Splash Screen", color = Color.White)
    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("bottom_nav") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }
}