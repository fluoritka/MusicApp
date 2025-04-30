package com.example.musicapp.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicapp.model.Track
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel
import com.example.musicapp.viewmodel.HomeViewModel
import com.example.musicapp.viewmodel.SearchViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authVm: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        /* ---------- AUTH ---------- */
        composable("login") {
            LoginScreen(
                viewModel = authVm,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterNav = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authVm,
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBackToLogin    = { navController.popBackStack() }
            )
        }

        /* ---------- HOME ---------- */
        composable("home") {
            val homeVm: HomeViewModel = viewModel()
            HomeScreen(
                onPlayTrack = { _: Track -> navController.navigate("player") },
                onAlbumClick = { id -> navController.navigate("album/$id") },
                authVm  = authVm,
                homeVm  = homeVm
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

        /* ---------- ALBUM ---------- */
        composable(
            route = "album/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId") ?: ""
            AlbumScreen(
                userId = userId,
                onTrackClick = { navController.navigate("player") }
            )
        }

        /* ---------- PLAYER ---------- */
        composable("player") {
            PlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}
