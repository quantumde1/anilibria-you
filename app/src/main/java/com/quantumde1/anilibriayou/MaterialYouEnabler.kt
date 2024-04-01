package com.quantumde1.anilibriayou

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OpenUrlButton(text: String, url: String) {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
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
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(DARK_THEME_KEY, darkTheme)
            putBoolean(DYNAMIC_COLORS_KEY, dynamicColors)
            apply()
        }
    }

    fun getSettings(context: Context): Pair<Boolean, Boolean> {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val darkTheme = sharedPref.getBoolean(DARK_THEME_KEY, false)
        val dynamicColors = sharedPref.getBoolean(DYNAMIC_COLORS_KEY, false)
        return Pair(darkTheme, dynamicColors)
    }
}

@Composable
fun MyDynamicTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val (darkThemeEnabled, dynamicColorsEnabled) = LocalThemeSettings.current

    val colorScheme = when {
        dynamicColorsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkThemeEnabled -> {
            dynamicDarkColorScheme(context)
        }

        dynamicColorsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkThemeEnabled -> {
            dynamicLightColorScheme(context)
        }

        darkThemeEnabled -> {
            darkColorScheme() // Your dark color scheme when dynamic colors are disabled
        }

        else -> {
            lightColorScheme() // Your light color scheme when dynamic colors are disabled
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Define your typography here
        content = content
    )
}

@Composable
fun SettingsScreen(onThemeChange: (Boolean, Boolean) -> Unit) {
    val context = LocalContext.current
    val (themeSettings, setThemeSettings) = remember {
        mutableStateOf(
            PreferencesManager.getSettings(
                context
            )
        )
    }

    var darkTheme by remember { mutableStateOf(themeSettings.first) }
    var dynamicColors by remember { mutableStateOf(themeSettings.second) }
    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LaunchedEffect(key1 = themeSettings) {
                PreferencesManager.saveSettings(context, themeSettings.first, themeSettings.second)
                onThemeChange(themeSettings.first, themeSettings.second)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.padding(2.dp))
                Text("Настройки", fontSize = 25.sp)
                Spacer(modifier = Modifier.padding(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Темная тема", modifier = Modifier.weight(1f))
                    Switch(checked = darkTheme, onCheckedChange = {
                        darkTheme = it
                        onThemeChange(darkTheme, dynamicColors)
                    })
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Динамические цвета (Android 12+)", modifier = Modifier.weight(1f))
                        Switch(checked = dynamicColors, onCheckedChange = {
                            dynamicColors = it
                            onThemeChange(darkTheme, dynamicColors)
                        })
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Text(text = "Сделано quantumde1, никакие права не защищены.")
                Spacer(modifier = Modifier.padding(4.dp))
                OpenUrlButton(
                    text = "Matrix",
                    url = "https://matrix.to/#/@quantumde1:underlevel.ddns.net"
                )
                Spacer(modifier = Modifier.padding(4.dp))
                OpenUrlButton(text = "Telegram", url = "https://t.me/quantumde1")
            }
        }
    }
}