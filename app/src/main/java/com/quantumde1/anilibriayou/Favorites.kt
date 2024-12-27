package com.quantumde1.anilibriayou

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anime_preferences")

object PreferencesKeys {
    val FAVORITE_ANIME_TITLES_KEY = stringSetPreferencesKey("favorite_anime_titles")
}


class DataStoreRepository(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveFavoriteAnimeTitleId(id: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_ANIME_TITLES_KEY] ?: setOf()
            preferences[PreferencesKeys.FAVORITE_ANIME_TITLES_KEY] =
                currentFavorites + id.toString()
        }
    }

    suspend fun removeFavoriteAnimeTitleId(id: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_ANIME_TITLES_KEY] ?: setOf()
            preferences[PreferencesKeys.FAVORITE_ANIME_TITLES_KEY] =
                currentFavorites - id.toString()
        }
    }

    val favoriteAnimeTitleIds: Flow<Set<Int>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_ANIME_TITLES_KEY]?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: setOf()
        }
}
