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
import com.example.musicapp.model.Track
import com.example.musicapp.viewmodel.HomeViewModel
import com.example.musicapp.viewmodel.SearchViewModel
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.ui.theme.viewmodel.PlayerViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // общий Auth и Player VM из Activity
    val authVm: AuthViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)
    val playerVm: PlayerViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)

    NavHost(
        navController    = navController,
        startDestination = "login",
        modifier         = modifier
    ) {
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
        composable("home") {
            val homeVm: HomeViewModel = viewModel()
            HomeScreen(
                navController = navController,
                authVm        = authVm,
                homeVm        = homeVm
            )
        }
        composable("search") {
            SearchScreen()
        }
        composable("library") {
            LibraryScreen(
                onOpenPlaylist = { pid -> navController.navigate("plist/$pid") }
            )
        }
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
        composable(
            route     = "album/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId").orEmpty()
            AlbumScreen(
                userId       = userId,
                onTrackClick = { navController.navigate("player") }
            )
        }
        composable("player") {
            PlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}
