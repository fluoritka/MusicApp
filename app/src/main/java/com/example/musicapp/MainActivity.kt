package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding      // ← новый импорт
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.theme.screens.ModernMiniPlayerBar
import com.example.musicapp.ui.theme.screens.NavGraph
import com.example.musicapp.ui.theme.theme.MusicAppTheme
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                val playerVm: PlayerViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        LocalContext.current as ViewModelStoreOwner
                    )

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        Column {
                            val current  by playerVm.currentTrack.collectAsState()
                            val playing  by playerVm.isPlaying.collectAsState()
                            val progress by playerVm.progress.collectAsState()

                            current?.let { track ->
                                ModernMiniPlayerBar(
                                    track             = track,
                                    isPlaying         = playing,
                                    progress          = progress,
                                    onProgressChange  = playerVm::seekTo,
                                    onSkipPrevious    = {},
                                    onPlayPauseToggle = playerVm::toggle,
                                    onSkipNext        = {},
                                    onPlayerClick     = { navController.navigate("player") },
                                    modifier          = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                ) { inner ->
                    NavGraph(
                        navController = navController,
                        modifier      = Modifier.padding(inner)   // ← padding работает
                    )
                }
            }
        }
    }
}
