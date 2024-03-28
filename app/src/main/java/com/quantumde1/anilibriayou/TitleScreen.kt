package com.quantumde1.anilibriayou

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

sealed class AnimeTitleState {
    object Loading : AnimeTitleState()
    data class Success(val title: Title) : AnimeTitleState()
    data class Failure(val exception: Exception) : AnimeTitleState()
}

suspend fun loadAnimeTitle(id: Int): Title? {
    // Binds apiService with your code
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
                try {
                    val title = loadAnimeTitle(id)
                    if (title != null) {
                        animeTitleState = AnimeTitleState.Success(title)
                    } else {
                        animeTitleState = AnimeTitleState.Failure(Exception("Title is null"))
                    }
                } catch (e: Exception) {
                    animeTitleState = AnimeTitleState.Failure(e)
                }
            }
            when (animeTitleState) {
                is AnimeTitleState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AnimeTitleState.Success -> {
                    (animeTitleState as AnimeTitleState.Success).title.let { title ->
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            CustomTopAppBar("", onBackClicked = { navController.navigateUp() } )
                            Row(modifier = Modifier.fillMaxWidth()) {
                                val configuration = LocalConfiguration.current
                                val screenWidth = configuration.screenWidthDp.dp
                                val screenHeight = configuration.screenHeightDp.dp
                                val cardWidth = screenWidth * 0.5f // 50% of screen width
                                val cardHeight =
                                    screenHeight * 0.375f // 37.5% of screen height, which is 3/8

                                Card(
                                    modifier = Modifier
                                        .width(cardWidth)
                                        .height(cardHeight)
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(15.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()

                                    ) {
                                        val imageUrl =
                                            "https://static.wwnd.space/${title.posters.original.url}"
                                        Image(
                                            painter = rememberImagePainter(imageUrl,
                                                builder = {
                                                    listener(onError = { _, throwable ->
                                                        Log.e(
                                                            "ImageCard",
                                                            "Error loading image",
                                                            throwable.throwable
                                                        )
                                                    })// Replace with your error drawable resource
                                                }), // Use Coil to load the image from the URL
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = title.names.ru,
                                        style = LocalTextStyle.current.copy(fontSize = 20.sp)
                                    )
                                    Text(
                                        text = "Сезон выхода:\n" + title.season.string + "\nГод:\n" + title.season.year + "\nСерий:\n" + title.type.episodes
                                        + " по " + title.type.length + " минуты",
                                        style = LocalTextStyle.current.copy(fontSize = 16.sp)
                                    )
                                    Spacer(modifier = Modifier.padding(6.dp))

                                    Button(onClick = {
                                            navController.navigate("episodesList/$id")
                                    },
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(text = "Смотреть")
                                    }
                                }
                            }

                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text (text = "Описание", style = LocalTextStyle.current.copy(fontSize = 19.sp))
                                        Spacer(modifier = Modifier.padding(2.dp))
                                        Text(
                                            text = title.description,
                                            style = LocalTextStyle.current.copy(fontSize = 16.sp)
                                        )
                                    }
                                }
                            }
                        }
                        is AnimeTitleState.Failure -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (animeTitleState as AnimeTitleState.Failure).exception.message
                                    ?: "An error occurred.",
                            )
                        }
                    }
                    }
                }
            }
}