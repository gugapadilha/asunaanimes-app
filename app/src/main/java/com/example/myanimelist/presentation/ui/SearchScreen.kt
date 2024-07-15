package com.example.myanimelist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.SearchBox


@Composable
fun SearchScreen(navController : NavHostController) {
    val animeRepository = AnimeRepository()
    val getAllAnimeData = animeRepository.getAllData()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBox()

        LazyColumn {
            items(getAllAnimeData) { anime ->
                AnimeItem(anime = anime)
            }

        }
    }
}