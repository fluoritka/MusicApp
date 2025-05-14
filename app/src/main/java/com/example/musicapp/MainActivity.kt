package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.theme.screens.ModernMiniPlayerBar
import com.example.musicapp.ui.theme.screens.NavGraph
import com.example.musicapp.ui.theme.theme.MusicAppTheme
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
                val backEntry    by navController.currentBackStackEntryAsState()
                val destination   = backEntry?.destination?.route ?: ""

                Scaffold(
                    bottomBar = {
                        Column {
                            /* --- mini-player --- */
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

                            /* --- bottom nav --- */
                            if (destination in listOf("home", "search", "library")) {
                                NavigationBar {
                                    NavItem(
                                        icon   = Icons.Filled.Home,
                                        label  = "Home",
                                        route  = "home",
                                        currentRoute = destination,
                                        onClick = { navController.navigate("home") }
                                    )
                                    NavItem(
                                        icon   = Icons.Filled.Search,
                                        label  = "Search",
                                        route  = "search",
                                        currentRoute = destination,
                                        onClick = { navController.navigate("search") }
                                    )
                                    NavItem(
                                        icon   = Icons.Filled.LibraryMusic,
                                        label  = "Library",
                                        route  = "library",
                                        currentRoute = destination,
                                        onClick = { navController.navigate("library") }
                                    )
                                }
                            }
                        }
                    }
                ) { inner ->
                    NavGraph(
                        navController = navController,
                        modifier      = Modifier.padding(inner)
                    )
                }
            }
        }
    }
}

/* ----------------------------------------------------------- */
@Composable
private fun RowScope.NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
