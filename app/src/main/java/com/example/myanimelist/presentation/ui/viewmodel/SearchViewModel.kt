package com.example.myanimelist.presentation.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model.Data

import android.util.Log

class SearchViewModel : ViewModel() {
    val animeList = mutableStateListOf<Data>()
    val previousSearches = mutableStateOf<List<String>>(listOf())
    private val animeService = AnimeService.create()

    fun saveSearchQueryToStorage(query: String, context: Context) {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Adicionar a nova pesquisa e manter apenas as 5 mais recentes
        savedQueries.remove(query) // Remove a query se já existir (evita duplicatas)
        savedQueries.add(query)

        // Ordena as pesquisas pela ordem de inserção e limita a 5
        val sortedQueries = savedQueries.sortedByDescending { savedQueries.indexOf(it) }.take(5).toSet()

        val success = sharedPref.edit().putStringSet("queries", sortedQueries).commit()
        if (success) {
            Log.d("SearchViewModel", "Query '$query' saved successfully.")
        } else {
            Log.e("SearchViewModel", "Failed to save query '$query'.")
        }

        // Atualizar a variável `previousSearches` na ViewModel para refletir as mudanças
        previousSearches.value = sortedQueries.toList()
    }

    fun loadPreviousSearchesFromStorage(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val savedQueries = sharedPref.getStringSet("queries", mutableSetOf())?.toList() ?: listOf()

        // Ordena as pesquisas pela ordem de inserção e limita a 5
        val sortedQueries = savedQueries.sortedByDescending { savedQueries.indexOf(it) }.take(5)

        Log.d("SearchViewModel", "Loaded queries: $sortedQueries")
        previousSearches.value = sortedQueries
        return sortedQueries
    }
}
