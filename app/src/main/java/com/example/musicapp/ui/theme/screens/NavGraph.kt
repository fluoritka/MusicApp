// app/src/main/java/com/example/musicapp/ui/theme/screens/NavGraph.kt
package com.example.musicapp.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicapp.ui.theme.screens.LoginScreen
import com.example.musicapp.ui.theme.screens.RegisterScreen
import com.example.musicapp.ui.theme.screens.HomeScreen
import com.example.musicapp.ui.theme.screens.SearchScreen
import com.example.musicapp.ui.theme.screens.LibraryScreen
import com.example.musicapp.ui.theme.screens.PlaylistDetailScreen
import com.example.musicapp.ui.theme.screens.AlbumScreen
import com.example.musicapp.ui.theme.screens.PlayerScreen
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel
import com.example.musicapp.viewmodel.HomeViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Shared ViewModels
    val authVm   = viewModel<AuthViewModel>(LocalContext.current as ViewModelStoreOwner)
    val playerVm = viewModel<PlayerViewModel>(LocalContext.current as ViewModelStoreOwner)

    NavHost(
        navController    = navController,
        startDestination = "login",
        modifier         = modifier
    ) {
        // Login
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

        // Register
        composable("register") {
            RegisterScreen(
                viewModel          = authVm,
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBackToLogin     = { navController.popBackStack() }
            )
        }

        // Home
        composable("home") {
            val homeVm: HomeViewModel = viewModel()
            HomeScreen(
                navController = navController,
                authVm        = authVm,
                homeVm        = homeVm
            )
        }

        // Search
        composable("search") {
            SearchScreen()
        }

        // Library
        composable("library") {
            LibraryScreen(
                onOpenPlaylist = { pid -> navController.navigate("plist/$pid") }
            )
        }

        // Playlist Details
        composable(
            route     = "plist/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) { back ->
            val pid = back.arguments?.getString("pid").orEmpty()
            PlaylistDetailScreen(
                playlistId    = pid,
                onBack        = { navController.popBackStack() },
                onPlayerClick = { navController.navigate("player") }
            )
        }

        // Album (including Recently Played as userId="recent")
        composable(
            route     = "album/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId").orEmpty()
            AlbumScreen(
                userId        = userId,
                onTrackClick = { navController.navigate("player") }
            )
        }

        // Player
        composable("player") {
            PlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}
