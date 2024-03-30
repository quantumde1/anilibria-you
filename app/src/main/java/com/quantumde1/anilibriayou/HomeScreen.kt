package com.quantumde1.anilibriayou

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(0) }
    val tabTitles = listOf("Обновленные", "Избранные")

    MyDynamicTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tabs
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { setSelectedTabIndex(index) },
                            text = { Text(title) }
                        )
                    }
                }

                // Content for the selected tab
                when (selectedTabIndex) {
                    1 -> FavoritesTabContent() // Replace with your actual favorites content composable
                    0 -> UpdatesTabContent(viewModel, navController) // Pass the viewModel and navController to the UpdatesTabContent
                }

                // Rest of your HomeScreen content
                // ...
            }
        }
    }
}

@Composable
fun FavoritesTabContent() {
    // TODO: Implement the UI for the favorites tab
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Favorites content goes here")
    }
}

@Composable
fun UpdatesTabContent(viewModel: MainViewModel, navController: NavController) {
    val titles by viewModel.titles.observeAsState(initial = emptyList())
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isSearchActive = remember { mutableStateOf(false) }

    Column {
        // Search TextField
        TextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                isSearchActive.value = it.isNotEmpty()
                if (it.isNotEmpty()) {
                    viewModel.searchTitles(it) // Perform search with the current query
                }
            },
            label = { Text("Поиск") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            },
            trailingIcon = {
                if (isSearchActive.value) {
                    IconButton(onClick = {
                        // Handle the back action
                        isSearchActive.value = false
                        searchQuery.value = ""
                        keyboardController?.hide() // Dismiss the keyboard
                        viewModel.searchTitles("") // Reset search
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchTitles(searchQuery.value)
                isSearchActive.value = true
                keyboardController?.hide() // Dismiss the keyboard
            })
        )

        // Content based on search results or the full list
        TitleListContent(titles, navController)
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleListContent(titles: List<Title>, navController: NavController) {
    // Check if the titles list is empty and display the appropriate UI
    if (titles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No titles available")
        }
    } else {
        // Title List Screen
        FlowRow(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            titles.forEach { title ->
                ImageCard(navController, title)
            }
        }
    }
}