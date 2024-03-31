package com.quantumde1.anilibriayou

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(text: String, onBackClicked: () -> Unit) {
    TopAppBar(
        title = { Text(text) },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun LeftAlignedTextButton(episode: Episode, navController: NavController, num: Int, uri: String) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val buttonHeight = screenHeight / 14
    val videoQualities = listOf("sd", "hd", "fhd") // Define available video qualities
    var expanded by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf(videoQualities[0]) } // Default quality

    TextButton(
        onClick = { expanded = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight),
        shape = RectangleShape // This makes the corners square
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Add padding to the Row if needed
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$num Серия")
            Spacer(Modifier.weight(1f)) // This will push the dropdown to the end of the row
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                videoQualities.forEach { quality ->
                    DropdownMenuItem(onClick = {
                        selectedQuality = quality
                        expanded = false
                        val newUri = when (quality) {
                            "sd" -> uri + episode.hls.sd
                            "hd" -> uri + episode.hls.hd
                            "fhd" -> uri + episode.hls.fhd
                            else -> uri + episode.hls.sd
                        }
                        val encodedUriWithQuality = Uri.encode("$newUri")
                        navController.navigate("lonePlayer/$encodedUriWithQuality")
                    }, text = { Text(quality) })
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EpisodesList(
    navController: NavController,
    onBackClicked: () -> Unit,
    animeId: Int,
    uri: Comparable<*>
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                CustomTopAppBar("Выберите серию", onBackClicked = onBackClicked)

                val viewModel: MainViewModel = viewModel()
                val episodes by viewModel.episodes.observeAsState(initial = emptyList())

                LaunchedEffect(key1 = animeId) {
                    viewModel.fetchEpisodes(animeId)
                }

                LazyColumn {
                    items(episodes) { episode ->
                        LeftAlignedTextButton(
                            episode,
                            navController,
                            episode.episode,
                            "https://cache.libria.fun"
                        )
                        // Add more UI elements to display other episode details
                    }
                }
            }
        }
    }
}