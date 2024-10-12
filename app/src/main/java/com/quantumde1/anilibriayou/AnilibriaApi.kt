package com.quantumde1.anilibriayou

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
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

    private val _episodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> = _episodes

    private val _favoriteTitleIds = MutableLiveData<Set<Int>>()
    val favoriteTitleIds: LiveData<Set<Int>> = _favoriteTitleIds

    private val _favoriteTitles = MutableLiveData<List<Title>>()
    val favoriteTitles: LiveData<List<Title>> = _favoriteTitles

    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.anilibria.tv")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnilibriaApiService::class.java)

    init {
        fetchTitles(30)
        fetchFavoriteTitleIds()
        fetchFavoriteTitles()
    }

    private fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>, onSuccess: (T) -> Unit) {
        try {
            val response = apiCall()
            response.body()?.let { onSuccess(it) } ?: run {
                logError("MainViewModel", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            logError("MainViewModel", "Error during API call", e)
        }
    }

    private fun fetchTitles(itemsPerPage: Int) {
        viewModelScope.launch {
            safeApiCall({ apiService.getTitleList(itemsPerPage) }) { titleListResponse ->
                _titles.postValue(titleListResponse.list)
            }
        }
    }

    fun fetchEpisodes(animeId: Int) {
        viewModelScope.launch {
            safeApiCall({ apiService.getAnimeDetails(animeId) }) { title ->
                val episodesList = title.player?.list?.values?.toList() ?: emptyList()
                _episodes.postValue(episodesList)
            }
        }
    }

    private fun fetchFavoriteTitleIds() {
        viewModelScope.launch {
            dataStoreRepository.favoriteAnimeTitleIds.collect { favoriteIds ->
                _favoriteTitleIds.postValue(favoriteIds)
            }
        }
    }

    fun fetchFavoriteTitles() {
        viewModelScope.launch {
            val favoriteIds = dataStoreRepository.favoriteAnimeTitleIds.first()
            val titlesList = mutableListOf<Title>()
            favoriteIds.forEach { id ->
                safeApiCall({ apiService.getAnimeDetails(id) }) { title ->
                    titlesList.add(title)
                }
            }
            _favoriteTitles.postValue(titlesList)
        }
    }

    fun saveFavoriteAnimeTitleId(id: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveFavoriteAnimeTitleId(id)
        }
    }

    fun removeFavoriteAnimeTitleId(id: Int) {
        viewModelScope.launch {
            dataStoreRepository.removeFavoriteAnimeTitleId(id)
        }
    }

    fun searchTitles(searchQuery: String) {
        viewModelScope.launch {
            safeApiCall({ apiService.searchTitles(searchQuery) }) { titleListResponse ->
                _titles.postValue(titleListResponse.list)
            }
        }
    }

    fun searchTitlesWithGenres(searchQuery: String, selectedGenres: List<String>) {
        // Implement the search logic with genres if needed
        // This function can be optimized similarly to the others
    }
}