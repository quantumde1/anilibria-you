package com.quantumde1.anilibriayou

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val (themeSettings, setThemeSettings) = remember {
        mutableStateOf(
            PreferencesManager.getSettings(
                context
            )
        )
    }

    MyDynamicTheme {
        val items = listOf(
            NavigationItem("Релизы", Icons.Filled.Home, "home"),
            NavigationItem("Профиль", Icons.Filled.Person, "profile"),
            NavigationItem("Настройки", Icons.Filled.Settings, "settings")
        )

        NavigationBar(modifier = modifier) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                val selected = item.route == currentRoute
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = selected,
                    onClick = {
                        when (item.route) {
                            "home" -> navController.navigate("home") {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }

                            "settings" -> navController.navigate("settings")
                            "profile" -> navController.navigate("profile")
                            // For other routes, simply navigate up
                            else -> navController.navigateUp()
                        }
                    }
                )
            }
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector, val route: String)