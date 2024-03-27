package com.quantumde1.anilibriayou

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(viewModel: MainViewModel = viewModel()) {

    val titles by viewModel.titles.observeAsState(initial = emptyList())
    Log.d("HomeScreen", "HomeScreen composable is being recomposed")

    if (titles.isEmpty()) {
        Log.d("HomeScreen", "Titles list is empty")
    } else {
        Log.d("HomeScreen", "Titles list has ${titles.size} items")
    }
    MyDynamicTheme {
        if (titles.isEmpty()) {
            Text("No titles available")
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                FlowRow(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    titles.forEach { title ->
                        val ImageUrl = "https://static.wwnd.space/${title.posters.original.url}";
                        Log.d("HomeScreen", "Loading image from: $ImageUrl")
                        ImageCard(
                            imageUrl = ImageUrl,
                            cardText = title.names.ru
                        )
                    }
                }
            }
        }
    }
}