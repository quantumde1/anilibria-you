package com.quantumde1.anilibriayou

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material3.OutlinedTextField

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
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
                    1 -> FavoritesTabContent()
                    0 -> UpdatesTabContent(viewModel, navController)
                }
            }
        }
    }
}

fun FavoritesTabContent() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesTabContent(viewModel: MainViewModel, navController: NavController) {
    val titles by viewModel.titles.observeAsState(initial = emptyList())
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isSearchActive = remember { mutableStateOf(false) }
    val expanded = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    // Example list of genres, this should come from your view model
    val genres =
        listOf("Комедия", "Романтика", "Драма", "Приключение", "Школа", "Триллер", "Музыка")

    // State for selected genres, this should also be managed by your view model
    val selectedGenres = remember { mutableStateListOf<String>() }

    Column {
        // Search TextField
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                isSearchActive.value = it.isNotEmpty()
                viewModel.searchTitles(it) // Perform search with the current query or reset if empty
            },
            label = { Text("Поиск") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            },
            trailingIcon = {
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Фильтры"
                        )
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
            }),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors() // Use default Material 3 colors
        )
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Выберите жанры") },
                text = {
                    // List of checkboxes for each genre
                    Column {
                        genres.forEach { genre ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selectedGenres.contains(genre),
                                    onCheckedChange = { isSelected ->
                                        if (isSelected) {
                                            selectedGenres.add(genre)
                                        } else {
                                            selectedGenres.remove(genre)
                                        }
                                    }
                                )
                                Text(text = genre, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Update the search with the new list of selected genres
                            viewModel.searchTitlesWithGenres(searchQuery.value, selectedGenres)
                            showDialog.value = false
                        }
                    ) {
                        Text("Далее")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
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