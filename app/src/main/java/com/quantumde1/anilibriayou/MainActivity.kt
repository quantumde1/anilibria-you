package com.quantumde1.anilibriayou

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Query

data class TitleListResponse(
    val list: List<Title>
)

data class Title(
    val id: Int,
    val code: String,
    val names: Names,
    val posters: Posters,
    // ... include other fields as necessary
)

data class Names(
    val ru: String,
    val en: String,
    val alternative: String?
)

data class Posters(
    val small: Poster,
    val medium: Poster,
    val original: Poster
)

data class Poster(
    val url: String,
    val raw_base64_file: String?
)

data class Pagination(
    val pages: Int,
    val current_page: Int,
    val items_per_page: Int,
    val total_items: Int
)

interface AnilibriaApiService {
    @GET("api/v3/title/updates")
    suspend fun getTitleList(@Query("items_per_page") itemsPerPage: Int): Response<TitleListResponse>
}

class MainViewModel : ViewModel() {
    private val _titles = MutableLiveData<List<Title>>()
    val titles: LiveData<List<Title>> = _titles

    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.anilibria.tv")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnilibriaApiService::class.java)

    init {
        fetchTitles(30)
    }

    private fun fetchTitles(itemsPerPage: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getTitleList(itemsPerPage)
                if (response.isSuccessful && response.body() != null) {
                    _titles.postValue(response.body()!!.list)
                } else {
                    Log.e(
                        "MainViewModel",
                        "Error fetching titles: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching titles", e)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyDynamicTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = if (darkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Define your typography here
        content = content
    )
}

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
                        val ImageUrl = "https://anilibria.tv${title.posters.original.url}";
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
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { MyBottomBar(navController) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("home") { HomeScreen() }
                    composable("favorites") { HomeScreen() }
                    composable("profile") { HomeScreen() }
                    // Add more destinations here if needed
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    MyDynamicTheme {
        val items = listOf(
            NavigationItem("Релизы", Icons.Filled.Home, "home"),
            NavigationItem("Избранное", Icons.Filled.Favorite, "favorites"),
            NavigationItem("Профиль", Icons.Filled.Person, "profile")
        )

        NavigationBar(modifier = modifier) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                val selected = item.route == currentRoute
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                // Avoid multiple copies of the same destination when reselecting the same item
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
data class NavigationItem(val title: String, val icon: ImageVector, val route: String)