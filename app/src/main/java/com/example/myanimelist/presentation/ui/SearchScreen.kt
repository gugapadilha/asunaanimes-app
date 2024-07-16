package com.example.myanimelist.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.SearchBox


@Composable
fun SearchScreen(navController : NavHostController) {
    val animeRepository = AnimeRepository()
    val getAllAnimeData = animeRepository.getAllData()

    val painter = rememberAsyncImagePainter(R.drawable.search_screen)

    val animeService = AnimeService.create()
    val call = animeService.getTopAnime()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds
        )
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
}