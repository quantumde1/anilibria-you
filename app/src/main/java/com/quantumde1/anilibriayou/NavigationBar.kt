package com.quantumde1.anilibriayou

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.mutableStateOf

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val (themeSettings, setThemeSettings) = remember { mutableStateOf(PreferencesManager.getSettings(context)) }

    MyDynamicTheme (){
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
                        if (item.route == "home") {
                            navController.navigate("home")
                        } else if (item.route == "settings") {
                            navController.navigate("settings")
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