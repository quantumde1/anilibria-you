package com.quantumde1.anilibriayou

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Обновленные", "Избранные")
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(LocalDataStoreRepository provides dataStoreRepository) {
        MyDynamicTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        if (dragAmount < 0 && selectedTabIndex != 1) {
                            setSelectedTabIndex(1)
                        } else {
                            setSelectedTabIndex(0)
                        }
                        change.consume() // Consume the change if you want to prevent further processing
                    }
                }) {
                    SearchField(searchQuery, viewModel, keyboardController)

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { setSelectedTabIndex(index) },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Плавный переход между вкладками
                    Crossfade(targetState = selectedTabIndex) { tabIndex ->
                        when (tabIndex) {
                            0 -> UpdatesTabContent(viewModel, navController)
                            1 -> FavoritesTabContent(navController, viewModel)
                            else -> UpdatesTabContent(viewModel, navController) // По умолчанию
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(
    searchQuery: MutableState<String>,
    viewModel: MainViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    TextField(
        value = searchQuery.value,
        onValueChange = {
            searchQuery.value = it
            viewModel.searchTitles(it)
        },
        placeholder = { Text("Поиск") },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            selectionColors = LocalTextSelectionColors.current,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
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
        }
    )
}

@Composable
fun FavoritesTabContent(navController: NavController, viewModel: MainViewModel) {
    val favoriteTitles by viewModel.favoriteTitles.observeAsState(initial = emptyList())
    EmptyStateContent(favoriteTitles.isEmpty(), "Нет добавленных в избранное аниме") {
        TitleListContent(favoriteTitles, navController)
    }
}

@Composable
fun UpdatesTabContent(viewModel: MainViewModel, navController: NavController) {
    val titles by viewModel.titles.observeAsState(initial = emptyList())
    EmptyStateContent(titles.isEmpty(), "No titles available") {
        TitleListContent(titles, navController)
    }
}

@Composable
fun EmptyStateContent(isEmpty: Boolean, message: String, content: @Composable () -> Unit) {
    if (isEmpty) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(message)
        }
    } else {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleListContent(titles: List<Title>, navController: NavController) {
    FlowRow(modifier = Modifier.verticalScroll(rememberScrollState())) {
        titles.forEach { title ->
            ImageCard(navController, title)
        }
    }
}