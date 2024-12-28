package com.quantumde1.anilibriayou

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyDynamicTheme(content: @Composable () -> Unit) {

    val context = LocalContext.current
    val (darkThemeEnabled, dynamicColorsEnabled) = LocalThemeSettings.current

    val colorScheme = when {
        dynamicColorsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkThemeEnabled) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkThemeEnabled -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeSettings =
                remember { mutableStateOf(PreferencesManager.getSettings(this)) }
            val context = LocalContext.current
            val dataStoreRepository = DataStoreRepository(context)
            CompositionLocalProvider(LocalThemeSettings provides themeSettings.value) {
                CompositionLocalProvider(LocalDataStoreRepository provides dataStoreRepository) {
                    MyDynamicTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {

                            val navController = rememberNavController()
                            Scaffold(
                                bottomBar = {
                                    MyBottomBar(navController)
                                },
                            ) { innerPadding ->
                                NavHost(
                                    navController = navController,
                                    startDestination = "home",
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable("home",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) { HomeScreen(navController) }

                                    composable("favorites",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) { HomeScreen(navController) }

                                    composable("settings",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) {
                                        SettingsScreen { darkTheme, dynamicColors ->
                                            themeSettings.value = Pair(darkTheme, dynamicColors)
                                            PreferencesManager.saveSettings(
                                                navController.context,
                                                darkTheme,
                                                dynamicColors
                                            )
                                        }
                                    }

                                    composable("animeDetails/{id}",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                    ) { backStackEntry ->
                                            AnimeDetailsScreen(
                                                navController = navController,
                                                id = backStackEntry.arguments?.getString("id")
                                                    ?.toIntOrNull() ?: 0
                                            )

                                    }

                                    composable("episodesList/{animeId}",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) { backStackEntry ->
                                            EpisodesList(
                                                navController = navController,
                                                onBackClicked = { navController.navigateUp() },
                                                animeId = backStackEntry.arguments?.getString("animeId")
                                                    ?.toIntOrNull() ?: -1,
                                                uri = backStackEntry.arguments?.getString("uri")
                                                    ?: ""
                                            )

                                    }

                                    composable("profile",
                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) {
                                        ProfileScreen()
                                    }

                                    composable("LonePlayer/{uri}",

                                        enterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleIn(
                                                initialScale = 0.9f,
                                                animationSpec = tween(durationMillis = 300)
                                            )

                                        },
                                        exitTransition = {
                                            fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                                                targetOffsetX = { it },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + scaleOut(
                                                targetScale = 0.9f, // End slightly smaller
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    ) { backStackEntry ->
                                            val uriArgument =
                                                backStackEntry.arguments?.getString("uri")
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
        }
    }
}

@Composable
fun SetSystemBarsColor() {
    MyDynamicTheme {
        val context = LocalContext.current
        val activity = context as? MainActivity // Cast to MainActivity
        val window = activity?.window // Get the window from the activity

        // Ensure the window is not null
        window?.let {
            val controller = WindowInsetsControllerCompat(it, it.decorView)

            // Set the status bar and navigation bar colors
            val statusBarColor = MaterialTheme.colorScheme.background.toArgb() // Use Material You color
            val navigationBarColor = MaterialTheme.colorScheme.background.toArgb() // Use Material You color

            // Determine if the current theme is light or dark
            val isLightTheme = Color(statusBarColor).luminance() > 0.5 // Check luminance for light theme

            // Set the appearance of status bar and navigation bar icons based on the theme
            controller.isAppearanceLightStatusBars = isLightTheme // Light icons for light theme
            controller.isAppearanceLightNavigationBars = isLightTheme // Light icons for light theme

            it.statusBarColor = statusBarColor
            it.navigationBarColor = navigationBarColor
        }
    }
}

@Composable
fun ProfileScreen() {
    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text("Soon!")
        }
    }
}