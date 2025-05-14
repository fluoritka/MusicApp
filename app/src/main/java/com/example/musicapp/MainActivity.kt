package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.theme.screens.ModernMiniPlayerBar
import com.example.musicapp.ui.theme.screens.NavGraph
import com.example.musicapp.ui.theme.theme.MusicAppTheme
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val playerVm: PlayerViewModel by viewModels()
    private val playlistVm: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                val navController = rememberNavController()
                val backEntry    by navController.currentBackStackEntryAsState()
                val destination  = backEntry?.destination?.route ?: ""

                Scaffold(
                    bottomBar = {
                        Column {
                            // Мини-плеер
                            val track    by playerVm.currentTrack.collectAsState()
                            val playing  by playerVm.isPlaying.collectAsState()
                            val progress by playerVm.progress.collectAsState()

                            track?.let {
                                ModernMiniPlayerBar(
                                    track             = it,
                                    isPlaying         = playing,
                                    progress          = progress,
                                    onProgressChange  = playerVm::seekTo,
                                    onSkipPrevious    = playerVm::skipPrevious,
                                    onPlayPauseToggle = playerVm::toggle,
                                    onSkipNext        = playerVm::skipNext,
                                    onPlayerClick     = { navController.navigate("player") },
                                    modifier          = Modifier.fillMaxWidth()
                                )
                            }

                            // Bottom navigation
                            if (destination in listOf("home", "search", "library")) {
                                NavigationBar {
                                    NavItem(
                                        icon         = Icons.Filled.Home,
                                        label        = "Home",
                                        route        = "home",
                                        currentRoute = destination,
                                        onClick      = { navController.navigate("home") }
                                    )
                                    NavItem(
                                        icon         = Icons.Filled.Search,
                                        label        = "Search",
                                        route        = "search",
                                        currentRoute = destination,
                                        onClick      = { navController.navigate("search") }
                                    )
                                    NavItem(
                                        icon         = Icons.Filled.LibraryMusic,
                                        label        = "Library",
                                        route        = "library",
                                        currentRoute = destination,
                                        onClick      = { navController.navigate("library") }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        playerVm      = playerVm,
                        playlistVm    = playlistVm,
                        modifier      = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    icon: ImageVector,
    label: String,
    route: String,
    currentRoute: String,
    onClick: () -> Unit
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor   = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
        selectedTextColor   = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
        indicatorColor      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
    )

    NavigationBarItem(
        selected = currentRoute == route,
        onClick  = onClick,
        icon     = { Icon(icon, contentDescription = label) },
        label    = { Text(label) },
        colors   = colors
    )
}
