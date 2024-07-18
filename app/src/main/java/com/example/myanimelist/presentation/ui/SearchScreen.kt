package com.example.myanimelist.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data
import com.example.myanimelist.domain.model2.TopAnime
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.SearchBox
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun SearchScreen(navController: NavHostController) {
    val (animeList, setAnimeList) = remember { mutableStateOf(emptyList<Data>()) }

    val painter = rememberAsyncImagePainter(R.drawable.search_screen)

    val animeService = AnimeService.create()
    val call = animeService.getTopAnime()

    DisposableEffect(Unit) {
        val callback = object : Callback<TopAnime> {
            override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                if (response.isSuccessful) {
                    val topAnime = response.body()
                    val animeListFromApi = topAnime?.data ?: emptyList()
                    setAnimeList(animeListFromApi)
                } else {
                    Log.e("SearchScreen", "Error to obtain top anime ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TopAnime>, t: Throwable) {
                Log.e("SearchScreen", "Requisition Failed: ${t.message}")
            }
        }
        call.enqueue(callback)

        onDispose {
            call.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SearchBox()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(animeList.chunked(3)) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (anime in rowItems) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            ) {
                                AnimeItem(anime = anime)
                            }
                        }
                    }
                }
            }
        }
    }
}
