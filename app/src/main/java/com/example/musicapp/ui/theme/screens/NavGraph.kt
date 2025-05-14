// app/src/main/java/com/example/musicapp/ui/theme/screens/NavGraph.kt
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicapp.model.Track
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.HomeViewModel
import com.example.musicapp.viewmodel.SearchViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authVm: AuthViewModel = viewModel()
    // Получаем общий PlayerViewModel в контексте Activity
    val playerVm: PlayerViewModel =
        viewModel(LocalContext.current as ViewModelStoreOwner)
    // Подписываемся на состояние плеера
    val currentTrack by playerVm.currentTrack.collectAsState()
    val isPlaying   by playerVm.isPlaying.collectAsState()
    val progress    by playerVm.progress.collectAsState()

    Scaffold(
        bottomBar = {
            currentTrack?.let { track ->
                ModernMiniPlayerBar(
                    track             = track,
                    isPlaying         = isPlaying,
                    progress          = progress,
                    onProgressChange  = playerVm::seekTo,
                    onSkipPrevious    = playerVm::skipPrevious,
                    onPlayPauseToggle = playerVm::toggle,
                    onSkipNext        = playerVm::skipNext,
                    onPlayerClick     = { navController.navigate("player") },
                    modifier          = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController     = navController,
            startDestination  = "login",
            modifier          = modifier.padding(innerPadding)
        ) {
            /* ---------- AUTH ---------- */
            composable("login") {
                LoginScreen(
                    viewModel      = authVm,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterNav  = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    viewModel          = authVm,
                    onRegisterSuccess = { navController.popBackStack("login", false) },
                    onBackToLogin     = { navController.popBackStack() }
                )
            }

            /* ---------- HOME ---------- */
            composable("home") {
                val homeVm: HomeViewModel = viewModel()
                HomeScreen(
                    onPlayTrack  = { _: Track -> navController.navigate("player") },
                    onAlbumClick = { id -> navController.navigate("album/$id") },
                    authVm       = authVm,
                    homeVm       = homeVm
                )
            }

            /* ---------- SEARCH ---------- */
            composable("search") {
                val searchVm: SearchViewModel = viewModel()
                SearchScreen(
                    viewModel     = searchVm,
                    authViewModel = authVm
                )
            }

            /* ---------- LIBRARY ---------- */
            composable("library") {
                LibraryScreen(
                    onOpenPlaylist = { pid -> navController.navigate("plist/$pid") }
                )
            }

            /* ---------- PLAYLIST DETAIL ---------- */
            composable(
                route     = "plist/{pid}",
                arguments = listOf(navArgument("pid") { type = NavType.StringType })
            ) { back ->
                val pid = back.arguments?.getString("pid") ?: ""
                PlaylistDetailScreen(
                    playlistId    = pid,
                    onBack        = { navController.popBackStack() },
                    onPlayerClick = { navController.navigate("player") }
                )
            }

            /* ---------- ALBUM ---------- */
            composable(
                route     = "album/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { back ->
                val userId = back.arguments?.getString("userId") ?: ""
                AlbumScreen(
                    userId       = userId,
                    onTrackClick = { navController.navigate("player") }
                )
            }

            /* ---------- PLAYER ---------- */
            composable("player") {
                PlayerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
