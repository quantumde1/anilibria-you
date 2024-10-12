package com.quantumde1.anilibriayou

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val dataStoreRepository = DataStoreRepository(context)
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(0) }
    val tabTitles = listOf("Обновленные", "Избранные")
    val titles by viewModel.titles.observeAsState(initial = emptyList())
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val showDialog = remember { mutableStateOf(false) } // Use mutable state here
    val genres = listOf("Комедия", "Романтика", "Драма", "Приключение", "Школа", "Триллер", "Музыка")
    val selectedGenres = remember { mutableStateListOf<String>() }

    CompositionLocalProvider(LocalDataStoreRepository provides dataStoreRepository) {
        MyDynamicTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchField(searchQuery, viewModel, keyboardController) { showDialog.value = true } // Update showDialog

                    if (showDialog.value) {
                        GenreSelectionDialog(showDialog, selectedGenres, searchQuery, viewModel) // Pass mutable state
                    }

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index, onClick = { setSelectedTabIndex(index) }, text = { Text(title) })
                        }
                    }

                    when (selectedTabIndex) {
                        1 -> FavoritesTabContent(navController)
                        0 -> UpdatesTabContent(viewModel, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun GenreSelectionDialog(
    showDialog: MutableState<Boolean>, // Change to MutableState<Boolean>
    selectedGenres: MutableList<String>,
    searchQuery: MutableState<String>,
    viewModel: MainViewModel,
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false }, // Update showDialog
            title = { Text(text = "Выберите жанры") },
            text = {
                Column {
                    val genres = listOf("Комедия", "Романтика", "Драма", "Приключение", "Школа", "Триллер", "Музыка")
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
                        viewModel.searchTitlesWithGenres(searchQuery.value, selectedGenres)
                        showDialog.value = false // Update showDialog
                    }
                ) {
                    Text("Далее")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) { // Update showDialog
                    Text("Отмена")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(searchQuery: MutableState<String>, viewModel: MainViewModel, keyboardController: SoftwareKeyboardController?, onShowDialog: () -> Unit) {
    TextField(
        value = searchQuery.value,
        onValueChange = {
            searchQuery.value = it
            viewModel.searchTitles(it)
        },
        placeholder = { Text("Поиск") },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .height(56.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            viewModel.searchTitles(searchQuery.value)
            keyboardController?.hide()
        }),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Поиск")
        },
        trailingIcon = {
            IconButton(onClick = onShowDialog) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Фильтры")
            }
        }
    )
}


@Composable
fun FavoritesTabContent(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val favoriteTitles by viewModel.favoriteTitles.observeAsState(initial = emptyList())

    if (favoriteTitles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет добавленных в избранное аниме")
        }
    } else {
        TitleFavoriteListContent(titles = favoriteTitles, navController = navController)
    }
}

@Composable
fun UpdatesTabContent(viewModel: MainViewModel, navController: NavController) {
    val titles by viewModel.titles.observeAsState(initial = emptyList())

    Column {
        TitleListContent(titles, navController)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleListContent(titles: List<Title>, navController: NavController) {
    if (titles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No titles available")
        }
    } else {
        FlowRow(modifier = Modifier.verticalScroll(rememberScrollState())) {
            titles.forEach { title ->
                ImageCard(navController, title)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleFavoriteListContent(titles: List<Title>, navController: NavController) {
    val dataStoreRepository = LocalDataStoreRepository.current
    val favoriteTitleIds by dataStoreRepository.favoriteAnimeTitleIds.collectAsState(initial = setOf())

    val favoriteTitles = titles.filter { it.id in favoriteTitleIds }

    if (favoriteTitles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет избранных аниме")
        }
    } else {
        FlowRow(modifier = Modifier.verticalScroll(rememberScrollState())) {
            favoriteTitles.forEach { title ->
                ImageCard(navController, title)
            }
        }
    }
}