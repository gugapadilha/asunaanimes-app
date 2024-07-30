package com.example.myanimelist.presentation.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    val animeList = mutableStateListOf<Data>()
    val previousSearches = mutableStateOf<List<String>>(listOf())
    private val animeService = AnimeService.create()

    fun loadPage(page: Int) {
        viewModelScope.launch {
            try {
                val topAnime = withContext(Dispatchers.IO) { animeService.getTopAnime(page) }
                val animeListFromApi = topAnime.data
                animeList.addAll(animeListFromApi)
            } catch (e: Exception) {
                // Handle error
            }
    }
    }

    fun searchAnime(query: String) {
        viewModelScope.launch {
            try {
                val searchedAnime = withContext(Dispatchers.IO) { animeService.getSearchedAnime(query) }
                animeList.clear()
                animeList.addAll(searchedAnime.data)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadPreviousSearches(context: Context) {
        viewModelScope.launch {
            previousSearches.value = loadPreviousSearchesFromStorage(context)
        }
    }

    fun saveSearchQuery(query: String, context: Context) {
        viewModelScope.launch {
            saveSearchQueryToStorage(query, context)
            previousSearches.value = loadPreviousSearchesFromStorage(context)
        }
    }

    private fun loadPreviousSearchesFromStorage(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf()) ?: mutableSetOf()
        return savedQueries.toList()
    }

    private fun saveSearchQueryToStorage(query: String, context: Context) {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf()) ?: mutableSetOf()
        savedQueries.add(query)
        sharedPref.edit().putStringSet("queries", savedQueries).apply()
    }
}
