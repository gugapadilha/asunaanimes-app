package com.example.myanimelist.presentation.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.myanimelist.presentation.util.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    val animeList = mutableStateListOf<Data>()
    val previousSearches = mutableStateOf<List<String>>(listOf())
    private val animeService = AnimeService.create()

    fun saveSearchQueryToStorage(query: String, context: Context) {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        savedQueries.add(query)
        val success = sharedPref.edit().putStringSet("queries", savedQueries).commit() // Use commit() instead of apply()
        if (success) {
            Log.d("SearchViewModel", "Query '$query' saved successfully.")
        } else {
            Log.e("SearchViewModel", "Failed to save query '$query'.")
        }
    }

    fun loadPreviousSearchesFromStorage(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf())?.toList() ?: listOf()
        Log.d("SearchViewModel", "Loaded queries: $savedQueries")
        return savedQueries
    }

}

object WatchedAnimeStore {
    private val _watchedAnimeList = mutableStateListOf<Data>()
    val watchedAnimeList: SnapshotStateList<Data> = _watchedAnimeList

    fun addAnime(anime: Data, context: Context) {
        _watchedAnimeList.add(anime)
        SharedPreferencesHelper.saveWatchedAnimes(context, _watchedAnimeList.toList())
    }

    fun removeAnime(anime: Data, context: Context) {
        _watchedAnimeList.remove(anime)
        SharedPreferencesHelper.saveWatchedAnimes(context, _watchedAnimeList.toList())
    }

    fun loadWatchedAnimes(context: Context) {
        val animes = SharedPreferencesHelper.getWatchedAnimes(context)
        _watchedAnimeList.clear()
        _watchedAnimeList.addAll(animes)
    }
}

object FavoriteAnimeStore {
    private val _favoriteAnimeList = mutableStateListOf<Data>()
    val favoriteAnimeList: SnapshotStateList<Data> = _favoriteAnimeList

    fun addAnime(anime: Data, context: Context) {
        _favoriteAnimeList.add(anime)
        SharedPreferencesHelper.saveFavoriteAnimes(context, _favoriteAnimeList.toList())
    }

    fun removeAnime(anime: Data, context: Context) {
        _favoriteAnimeList.remove(anime)
        SharedPreferencesHelper.saveFavoriteAnimes(context, _favoriteAnimeList.toList())
    }

    fun loadFavoriteAnimes(context: Context) {
        val animes = SharedPreferencesHelper.getFavoriteAnimes(context)
        _favoriteAnimeList.clear()
        _favoriteAnimeList.addAll(animes)
    }
}