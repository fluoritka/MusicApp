package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.theme.screens.NavGraph
import com.example.musicapp.ui.theme.theme.MusicAppTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Realm с миграцией: удаляем старую БД, если схема изменилась
        val config = RealmConfiguration.Builder(
            schema = setOf<KClass<out TypedRealmObject>>(
                com.example.musicapp.model.RealmUser::class,
                com.example.musicapp.model.SavedTrack::class
            )
        )
            .name("musicapp.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.open(config)

        setContent {
            MusicAppTheme {
                val navController = rememberNavController()
                val navEntry by navController.currentBackStackEntryAsState()
                val route = navEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (route == "home" || route == "search") {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.icon1_homepage),
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home") },
                                    selected = route == "home",
                                    onClick = { navController.navigate("home") }
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.icon2_search),
                                            contentDescription = "Search"
                                        )
                                    },
                                    label = { Text("Search") },
                                    selected = route == "search",
                                    onClick = { navController.navigate("search") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}