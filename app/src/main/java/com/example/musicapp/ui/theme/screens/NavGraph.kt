// Навигационный граф приложения: определяет все экраны и переходы
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
    // Инициализируем общие ViewModel: аутентификация и плеер
    val authVm   = viewModel<AuthViewModel>(LocalContext.current as ViewModelStoreOwner)
    val playerVm = viewModel<PlayerViewModel>(LocalContext.current as ViewModelStoreOwner)

    // Основной контейнер навигации с корневым маршрутом "login"
    NavHost(
        navController    = navController,
        startDestination = "login",
        modifier         = modifier
    ) {
        // Маршрут "login": экран входа в систему
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

        // Маршрут "register": экран регистрации нового пользователя
        composable("register") {
            RegisterScreen(
                viewModel          = authVm,
                onRegisterSuccess = { navController.popBackStack("login", false) },
                onBackToLogin     = { navController.popBackStack() }
            )
        }

        // Маршрут "home": главный экран с секциями альбомов
        composable("home") {
            val homeVm: HomeViewModel = viewModel()
            HomeScreen(
                navController = navController,
                authVm        = authVm,
                homeVm        = homeVm
            )
        }

        // Маршрут "search": экран поиска треков
        composable("search") {
            SearchScreen()
        }

        // Маршрут "library": экран библиотеки с плейлистами и избранным
        composable("library") {
            LibraryScreen(
                onOpenPlaylist = { pid -> navController.navigate("plist/$pid") }
            )
        }

        // Переход на детали плейлиста по ID
        composable(
            route     = "plist/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.StringType })
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("pid").orEmpty()
            PlaylistDetailScreen(
                playlistId    = pid,
                onBack        = { navController.popBackStack() },
                onPlayerClick = { navController.navigate("player") }
            )
        }

        // Маршрут "album/{userId}": экран альбомов (recent или пользовательские)
        composable(
            route     = "album/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            AlbumScreen(
                userId        = userId,
                onTrackClick = { navController.navigate("player") }
            )
        }

        // Маршрут "player": полноэкранный плеер трека
        composable("player") {
            PlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}
