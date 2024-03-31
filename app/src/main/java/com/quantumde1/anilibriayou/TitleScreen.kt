package com.quantumde1.anilibriayou

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// AnimeTitleState.kt
sealed class AnimeTitleState {
    object Loading : AnimeTitleState()
    data class Success(val title: Title) : AnimeTitleState()
    data class Failure(val exception: Exception) : AnimeTitleState()
}

// ApiService.kt
suspend fun loadAnimeTitle(id: Int): Title? {
    val apiService = Retrofit.Builder()
        .baseUrl("https://api.anilibria.tv")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnilibriaApiService::class.java)

    return try {
        val response = apiService.getAnimeDetails(id)
        response.body()
    } catch (e: Exception) {
        null
    }
}

// FoldableText.kt
@Composable
fun FoldableText(text: String, maxLength: Int = 200) {
    var isFolded by remember { mutableStateOf(true) }
    val displayText = if (isFolded && text.length > maxLength) text.take(maxLength) + "..." else text

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isFolded = !isFolded },
        shape = RectangleShape
    ) {
        Column {
            Spacer(modifier = Modifier.width(8.dp))
            FoldableTextHeader()
            Spacer(modifier = Modifier.padding(1.dp))
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.padding(3.dp))
        }
    }
}

@Composable
private fun FoldableTextHeader() {
    Row(
        modifier = Modifier.padding(all = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Toggle Text",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Описание",
            style = LocalTextStyle.current.copy(fontSize = 19.sp)
        )
    }
}

// AnimeDetailsScreen.kt
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AnimeDetailsScreen(navController: NavController, id: Int) {
    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var animeTitleState by remember { mutableStateOf<AnimeTitleState>(AnimeTitleState.Loading) }

            LaunchedEffect(key1 = id) {
                animeTitleState = try {
                    val title = loadAnimeTitle(id)
                    title?.let { AnimeTitleState.Success(it) } ?: AnimeTitleState.Failure(Exception("Title is null"))
                } catch (e: Exception) {
                    AnimeTitleState.Failure(e)
                }
            }

            AnimeDetailsContent(navController, animeTitleState)
        }
    }
}

@Composable
private fun AnimeDetailsContent(navController: NavController, animeTitleState: AnimeTitleState) {
    when (animeTitleState) {
        is AnimeTitleState.Loading -> LoadingIndicator()
        is AnimeTitleState.Success -> AnimeDetailsSuccessContent(navController, animeTitleState.title)
        is AnimeTitleState.Failure -> ErrorContent(animeTitleState.exception)
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AnimeDetailsSuccessContent(navController: NavController, title: Title) {
    Column {
        val truncatedText = if (title.names.ru.length > 20) {
            title.names.ru.take(20) + "..."
        } else {
            title.names.ru
        }
        CustomTopAppBar(
            truncatedText,
            onBackClicked = { navController.navigateUp() })

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            AnimeDetailsRow(navController, title)
            FoldableText(text = title.description)
        }
    }
}

@Composable
private fun AnimeDetailsRow(navController: NavController, title: Title) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the parent's size
                .padding(16.dp)
        ) {
            AnimeDetailsCard(title)
            AnimeDetailsText(title)
            AnimeDetailsButtons(navController, title.id)
        }
    }
}

@Composable
private fun AnimeDetailsCard(title: Title) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the parent's size
            .padding(16.dp)
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        val cardWidth = screenWidth * 0.600f // 50% of screen width
        val cardHeight = screenHeight * 0.45f // 37.5% of screen height, which is 3/8

        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .align(Alignment.Center), // Center the card within the Box
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            val imageUrl = "https://static.wwnd.space/${title.posters.original.url}"
            Image(
                painter = rememberImagePainter(imageUrl,
                    builder = {
                        listener(onError = { _, throwable ->
                            Log.e(
                                "ImageCard",
                                "Error loading image",
                                throwable.throwable
                            )
                        }) // Replace with your error drawable resource
                    }), // Use Coil to load the image from the URL
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun AnimeDetailsText(title: Title) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title.names.ru, fontWeight = FontWeight.Bold, fontSize = 24.sp,
                )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Сезон: ${title.season.string}, дата выхода: ${title.season.year}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Серий: ${title.type.episodes}",
            )
        }
    }
}


@Composable
private fun AnimeDetailsButtons(navController: NavController, id: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                navController.navigate("episodesList/$id")
            }
        ) {
            Text(text = "Смотреть")
        }
        Spacer(modifier = Modifier.width(8.dp)) // Add spacing between buttons
        Button(
            onClick = {
                // Implement the logic to add to favorites
            }
        ) {
            Text(text = "В избранное")
        }
    }
}



@Composable
private fun ErrorContent(exception: Exception) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = exception.message ?: "An error occurred.")
    }
}