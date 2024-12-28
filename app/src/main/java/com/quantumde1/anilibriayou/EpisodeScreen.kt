package com.quantumde1.anilibriayou

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
fun CustomTopAppBar(title: String, onBackClicked: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
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
fun LeftAlignedTextButton(episode: Episode, navController: NavController, num: Int, baseUri: String) {
    val configuration = LocalConfiguration.current
    val buttonHeight = configuration.screenHeightDp.dp / 14
    val videoQualities = listOf("sd", "hd", "fhd")
    var expanded by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf(videoQualities[0]) }

    TextButton(
        onClick = { expanded = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$num Серия")
            Spacer(Modifier.weight(1f))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                videoQualities.forEach { quality ->
                    DropdownMenuItem(onClick = {
                        selectedQuality = quality
                        expanded = false
                        val newUri = generateUri(baseUri, episode.hls, quality)
                        navController.navigate("lonePlayer/${Uri.encode(newUri)}")
                    }, text = { Text(quality) })
                }
            }
        }
    }
}

private fun generateUri(baseUri: String, hls: HLS, quality: String): String {
    return when (quality) {
        "sd" -> "$baseUri${hls.sd}"
        "hd" -> "$baseUri${hls.hd}"
        "fhd" -> "$baseUri${hls.fhd}"
        else -> "$baseUri${hls.sd}" // Fallback to SD
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EpisodesList(
    navController: NavController,
    onBackClicked: () -> Unit,
    animeId: Int,
    uri: String
) {

    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                CustomTopAppBar("Выберите серию", onBackClicked)

                val viewModel: MainViewModel = viewModel()
                val episodes by viewModel.episodes.observeAsState(initial = emptyList())

                LaunchedEffect(animeId) {
                    viewModel.fetchEpisodes(animeId)
                }

                LazyColumn {
                    items(episodes) { episode ->
                        LeftAlignedTextButton(
                            episode,
                            navController,
                            episode.episode,
                            uri
                        )
                    }
                }
            }
        }
    }
}