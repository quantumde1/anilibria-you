package com.quantumde1.anilibriayou

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    MyDynamicTheme {
        val items = remember {
            listOf(
                NavigationItem("Релизы", Icons.Filled.Home, "home"),
                NavigationItem("Профиль", Icons.Filled.Person, "profile"),
                NavigationItem("Настройки", Icons.Filled.Settings, "settings")
            )
        }
        SetSystemBarsColor()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val isVisible = currentRoute == "home" || currentRoute == "settings" || currentRoute == "profile"
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NavigationBar(modifier = modifier) {
                items.forEach { item ->
                    val selected = item.route == currentRoute
                    NavigationBarItem(
                        icon = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(if (selected) 28.dp else 24.dp) // Увеличиваем иконку при выборе
                                )
                            }
                        },
                        label = {
                            if (selected) {
                                Text(item.title) // Use Text composable for the label
                            }
                        },
                        selected = selected,
                        onClick = {
                            navigateToDestination(navController, item.route, currentRoute)
                        }
                    )
                }
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