package com.quantumde1.anilibriayou

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    MyDynamicTheme {
        val items = listOf(
            NavigationItem("Релизы", Icons.Filled.Home, "home"),
            NavigationItem("Избранное", Icons.Filled.Favorite, "favorites"),
            NavigationItem("Профиль", Icons.Filled.Person, "profile")
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
                        if (item.route == "home") {
                            navController.navigate("home")
                        } else if (item.route == "favorites") {
                            navController.navigate("favorites")
                        } else if (item.route == "profile") {
                            navController.navigate("profile")
                        } else if (item.route == "animeDetails") {
                            val animeId =
                                navController.previousBackStackEntry?.arguments?.getInt("animeId")
                            if (animeId != null) {
                                navController.navigate("animeDetailsScreen/$animeId")
                            }
                        } else if (item.route == "LonePlayer") {
                            val uri = navController.previousBackStackEntry?.arguments?.getString("uri")
                            val encodedUri = Uri.encode(uri)
                            navController.navigate("LonePlayer/$encodedUri")
                        }
                    }
                )
            }
        }
    }
}
data class NavigationItem(val title: String, val icon: ImageVector, val route: String)