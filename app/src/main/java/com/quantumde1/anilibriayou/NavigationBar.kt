package com.quantumde1.anilibriayou

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // If you don't need the settings, you can remove this
    // val settings = PreferencesManager.getSettings(context)

    MyDynamicTheme {
        val items = remember {
            listOf(
                NavigationItem("Релизы", Icons.Filled.Home, "home"),
                NavigationItem("Профиль", Icons.Filled.Person, "profile"),
                NavigationItem("Настройки", Icons.Filled.Settings, "settings")
            )

        }
        MyDynamicTheme {
            SetSystemBarsColor()
        }
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
                        navigateToDestination(navController, item.route, currentRoute)
                    }
                )
            }
        }
    }
}

private fun navigateToDestination(navController: NavController, route: String, currentRoute: String?) {
    if (currentRoute != route) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector, val route: String)