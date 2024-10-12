package com.quantumde1.anilibriayou

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OpenUrlButton(text: String, url: String) {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }) {
        Text(text)
    }
}

val LocalThemeSettings = compositionLocalOf { Pair(false, false) }

object PreferencesManager {
    private const val PREFERENCES_FILE_KEY = "com.quantumde1.anilibriayou.PREFERENCE_FILE_KEY"
    private const val DARK_THEME_KEY = "DARK_THEME_KEY"
    private const val DYNAMIC_COLORS_KEY = "DYNAMIC_COLORS_KEY"

    fun saveSettings(context: Context, darkTheme: Boolean, dynamicColors: Boolean) {
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE).edit().apply {
            putBoolean(DARK_THEME_KEY, darkTheme)
            putBoolean(DYNAMIC_COLORS_KEY, dynamicColors)
            apply()
        }
    }

    fun getSettings(context: Context): Pair<Boolean, Boolean> {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return Pair(
            sharedPref.getBoolean(DARK_THEME_KEY, false),
            sharedPref.getBoolean(DYNAMIC_COLORS_KEY, false)
        )
    }
}

@Composable
fun SettingsScreen(onThemeChange: (Boolean, Boolean) -> Unit) {
    val context = LocalContext.current
    val (darkTheme, dynamicColors) = remember { PreferencesManager.getSettings(context) }

    var isDarkTheme by remember { mutableStateOf(darkTheme) }
    var isDynamicColors by remember { mutableStateOf(dynamicColors) }

    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Настройки", fontSize = 25.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Темная тема", modifier = Modifier.weight(1f))
                    Switch(checked = isDarkTheme, onCheckedChange = {
                        isDarkTheme = it
                        PreferencesManager.saveSettings(context, isDarkTheme, isDynamicColors)
                        onThemeChange(isDarkTheme, isDynamicColors)
                    })
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Динамические цвета (Android 12+)", modifier = Modifier.weight(1f))
                        Switch(checked = isDynamicColors, onCheckedChange = {
                            isDynamicColors = it
                            PreferencesManager.saveSettings(context, isDarkTheme, isDynamicColors)
                            onThemeChange(isDarkTheme, isDynamicColors)
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Сделано quantumde1, никакие права не защищены.")
                Spacer(modifier = Modifier.height(4.dp))
                OpenUrlButton(
                    text = "Matrix",
                    url = "https://matrix.to/#/@quantumde1:underlevel.ddns.net"
                )
                Spacer(modifier = Modifier.height(4.dp))
                OpenUrlButton(text = "Telegram", url = "https://t.me/quantumde1")
            }
        }
    }
}