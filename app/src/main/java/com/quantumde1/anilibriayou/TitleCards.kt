package com.quantumde1.anilibriayou

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ImageCard(navController: NavController, title: Title) {
    val imageUrl = "https://static.wwnd.space/${title.posters.original.url}"
    val cardText = title.names.ru
    val truncatedText = if (cardText.length > 27) {
        cardText.take(27) + "..."
    } else {
        cardText
    }

    val configuration = LocalConfiguration.current
    val cardWidth = configuration.screenWidthDp.dp * 0.5f // 50% of screen width
    val cardHeight = configuration.screenHeightDp.dp * 0.375f // 37.5% of screen height

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(10.dp)
            .clickable {
                navController.navigate("animeDetails/${title.id}")
            },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .listener()
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = truncatedText,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = truncatedText,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 300f
                        )
                    )
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}