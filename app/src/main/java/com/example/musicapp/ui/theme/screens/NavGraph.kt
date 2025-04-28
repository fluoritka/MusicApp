package com.example.musicapp.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel

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
                onRegisterSuccess = {
                    // после регистрации — обратно на login
                    navController.popBackStack("login", inclusive = false)
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onGoToPlayer = { navController.navigate("player") },
                onAlbumClick = { userId -> navController.navigate("album/$userId") }
            )
        }

        composable("album/{userId}") { back ->
            val userId = back.arguments?.getString("userId") ?: ""
            AlbumScreen(
                userId = userId,
                onTrackClick = { navController.navigate("player") }
            )
        }

        composable("player") {
            PlayerScreen()
        }

        composable("search") {
            SearchScreen(viewModel = viewModel())
        }
    }
}
