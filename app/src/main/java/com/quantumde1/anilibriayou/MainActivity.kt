package com.quantumde1.anilibriayou

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeSettings = remember { mutableStateOf(PreferencesManager.getSettings(this)) }

            CompositionLocalProvider(LocalThemeSettings provides themeSettings.value) {
                val navController = rememberNavController()
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                Scaffold(
                    bottomBar = {
                        if (currentRoute != "LonePlayer/{uri}") {
                            MyBottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController) }
                        composable("favorites") { HomeScreen(navController) }
                        composable("settings") {
                            SettingsScreen(navController.context) { darkTheme, dynamicColors ->
                                themeSettings.value = Pair(darkTheme, dynamicColors)
                                PreferencesManager.saveSettings(
                                    navController.context,
                                    darkTheme,
                                    dynamicColors
                                )
                            }
                        }
                        composable("animeDetails/{id}") { backStackEntry ->
                            AnimeDetailsScreen(
                                navController = navController,
                                id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                            )
                        }
                        composable("episodesList/{animeId}") { backStackEntry ->
                            EpisodesList(
                                navController = navController,
                                onBackClicked = { navController.navigateUp() },
                                animeId = backStackEntry.arguments?.getString("animeId")
                                    ?.toIntOrNull()
                                    ?: -1, // Use -1 or another invalid ID as the default
                                uri = backStackEntry.arguments?.getString("uri")
                                    ?: "" // Use an empty string as the default
                            )
                        }
                        composable("LonePlayer/{uri}") { backStackEntry ->
                            val uriArgument = backStackEntry.arguments?.getString("uri")
                            if (uriArgument != null) {
                                LonePlayer(uriArgument)
                            }
                        }
                    }
                }
            }
        }
    }
}