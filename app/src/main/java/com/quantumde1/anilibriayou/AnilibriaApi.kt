package com.quantumde1.anilibriayou

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
