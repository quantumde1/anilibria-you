package com.quantumde1.anilibriayou

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface AnilibriaApiService {
    @GET("api/v3/title/updates")
    suspend fun getTitleList(@Query("items_per_page") itemsPerPage: Int): Response<TitleListResponse>

    @GET("api/v3/title/search")
    suspend fun searchTitles(@Query("search") searchQuery: String): Response<TitleListResponse>

    @GET("api/v3/title")
    suspend fun getAnimeDetails(@Query("id") id: Int): Response<Title>
}


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val dataStoreRepository = DataStoreRepository(context)

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

    fun fetchEpisodes(animeId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getAnimeDetails(animeId)
                if (response.isSuccessful && response.body() != null) {
                    val title = response.body()!!
                    val episodesList = title.player?.list?.values?.toList() ?: emptyList()
                    _episodes.postValue(episodesList)
                } else {
                    Log.e(
                        "MainViewModel",
                        "Error fetching episodes: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching episodes", e)
            }
        }
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

    private val _episodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> = _episodes
    // LiveData to hold the favorite titles
    private val _favoriteTitleIds = MutableLiveData<Set<Int>>()
    val favoriteTitleIds: LiveData<Set<Int>> = _favoriteTitleIds

    // Function to fetch favorite title IDs
    private fun fetchFavoriteTitleIds() {
        viewModelScope.launch {
            dataStoreRepository.favoriteAnimeTitleIds.collect { favoriteIds ->
                _favoriteTitleIds.postValue(favoriteIds)
            }
        }
    }
    // Function to save a title ID to favorites
    fun saveFavoriteAnimeTitleId(id: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveFavoriteAnimeTitleId(id)
        }
    }

    // Function to remove a title ID from favorites
    fun removeFavoriteAnimeTitleId(id: Int) {
        viewModelScope.launch {
            dataStoreRepository.removeFavoriteAnimeTitleId(id)
        }
    }
    init {
        fetchTitles(30)
        fetchFavoriteTitleIds()
    }

    // Function to fetch favorite titles from DataStore
    // ... existing init and fetchTitles functions
    fun searchTitlesWithGenres(searchQuery: String, selectedGenres: List<String>) {
        viewModelScope.launch {
            try {
                val genreFilter = selectedGenres.joinToString(",")
                val response = apiService.searchTitles("$searchQuery&genres=$genreFilter")
                // ... handle the response as before
            } catch (e: Exception) {
                // ... handle the exception as before
            }
        }
    }

    fun searchTitles(searchQuery: String) {
        viewModelScope.launch {
            try {
                val response = apiService.searchTitles(searchQuery)
                if (response.isSuccessful && response.body() != null) {
                    _titles.postValue(response.body()!!.list)
                } else {
                    Log.e(
                        "MainViewModel",
                        "Error searching titles: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error searching titles", e)
            }
        }
    }
}
