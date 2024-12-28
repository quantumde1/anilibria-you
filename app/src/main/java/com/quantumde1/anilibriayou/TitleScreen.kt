package com.quantumde1.anilibriayou

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

sealed class AnimeTitleState {
    object Loading : AnimeTitleState()
    data class Success(val title: Title) : AnimeTitleState()
    data class Failure(val exception: Exception) : AnimeTitleState()
}

suspend fun loadAnimeTitle(id: Int): Title? {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Set write timeout
        .build()
    val apiService = Retrofit.Builder()
        .baseUrl("https://api.anilibria.tv")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnilibriaApiService::class.java)

    return try {
        val response = apiService.getAnimeDetails(id)
        response.body()
    } catch (e: Exception) {
        Log.e("ApiService", "Error loading anime title", e)
        null
    }
}

@Composable
fun FoldableText(text: String, maxLength: Int = 200) {
    var isFolded by remember { mutableStateOf(true) }
    val displayText = if (isFolded && text.length > maxLength) text.take(maxLength) + "..." else text

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isFolded = !isFolded },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            FoldableTextHeader()
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun FoldableTextHeader() {
    Row(modifier = Modifier.padding(3.dp)) {
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Toggle Text", modifier = Modifier.size(24.dp))
        Text(text = "Описание", style = MaterialTheme.typography.titleMedium)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AnimeDetailsScreen(navController: NavController, id: Int) {
    MyDynamicTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            var animeTitleState by remember { mutableStateOf<AnimeTitleState>(AnimeTitleState.Loading) }

            LaunchedEffect(id) {
                animeTitleState = loadAnimeTitle(id)?.let { AnimeTitleState.Success(it) }
                    ?: AnimeTitleState.Failure(Exception("Title is null"))
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
        val truncatedText = title.names.ru.take(20) + if (title.names.ru.length > 20) "..." else ""
        CustomTopAppBar(truncatedText, onBackClicked = { navController.navigateUp() })

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AnimeDetailsCard(title)
            AnimeDetailsText(title)
            AnimeDetailsButtons(navController, title)
        }
    }
}

@Composable
private fun AnimeDetailsCard(title: Title) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val configuration = LocalConfiguration.current
        val cardWidth = configuration.screenWidthDp.dp * 0.6f
        val cardHeight = configuration.screenHeightDp.dp * 0.45f

        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .align(Alignment.Center),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            val imageUrl = "https://static.wwnd.space/${title.posters.original.url}"
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(imageUrl).apply {
                        listener()
                    }.build()
                ),
                contentDescription = null,
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
            Text(text = title.names.ru, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Сезон: ${title.season.string}, дата выхода: ${title.season.year}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Серий: ${title.type.episodes}")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

val LocalDataStoreRepository = compositionLocalOf<DataStoreRepository> {
    error("DataStoreRepository not provided")
}

@Composable
private fun AnimeDetailsButtons(navController: NavController, title: Title) {
    val dataStoreRepository = LocalDataStoreRepository.current
    val isFavorite = remember { mutableStateOf(title.isFavorite) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = { navController.navigate("episodesList/${title.id}") }) {
            Text(text = "Смотреть")
        }
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = {
            isFavorite.value = !isFavorite.value
            title.isFavorite = isFavorite.value
            CoroutineScope(Dispatchers.IO).launch {
                if (isFavorite.value) {
                    dataStoreRepository.saveFavoriteAnimeTitleId(title.id)
                } else {
                    dataStoreRepository.removeFavoriteAnimeTitleId(title.id)
                }
            }
        }) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite.value) "Удалить из избранного" else "Добавить в избранное",
                tint = if (isFavorite.value) Color.Red else Color.Gray
            )
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