package com.quantumde1.anilibriayou

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun ImageCard(navController: NavController, title: Title) {
    val imageUrl = "https://static.wwnd.space/${title.posters.original.url}"
    val cardText = title.names.ru
    val truncatedText = if (cardText.length > 20) {
        cardText.take(28) + "..."
    } else {
        cardText
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val cardWidth = screenWidth * 0.5f // 50% of screen width
    val cardHeight = screenHeight * 0.375f // 37.5% of screen height, which is 3/8

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
        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Image(
                painter = rememberImagePainter(imageUrl,
                    builder = {
                        listener(onError = { _, throwable ->
                            Log.e("ImageCard", "Error loading image", throwable.throwable)
                        })// Replace with your error drawable resource
                    }), // Use Coil to load the image from the URL
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