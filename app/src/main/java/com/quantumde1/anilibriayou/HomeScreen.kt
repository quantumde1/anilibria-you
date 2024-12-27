package com.quantumde1.anilibriayou

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(LocalDataStoreRepository provides dataStoreRepository) {
        MyDynamicTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchField(searchQuery, viewModel, keyboardController)

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { setSelectedTabIndex(index) },
                                text = { Text(title) })
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

@OptIn(ExperimentalMaterial3Api::class)
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
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
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
fun FavoritesTabContent(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val favoriteTitles by viewModel.favoriteTitles.observeAsState(initial = emptyList())
    EmptyStateContent(favoriteTitles.isEmpty(), "Нет добавленных в избранное аниме") {
        TitleFavoriteListContent(favoriteTitles, navController)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleFavoriteListContent(titles: List<Title>, navController: NavController) {
    val dataStoreRepository = LocalDataStoreRepository.current
    val favoriteTitleIds by dataStoreRepository.favoriteAnimeTitleIds.collectAsState(initial = setOf())

    val favoriteTitles = titles.filter { it.id in favoriteTitleIds }

    EmptyStateContent(favoriteTitles.isEmpty(), "Нет избранных аниме") {
        TitleListContent(favoriteTitles, navController)
    }
}