package com.example.myanimelist.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.data.repository.YearRepository
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.CustomWatched

@Composable
fun WatchedScreen(navController: NavHostController) {
    val painter = rememberAsyncImagePainter(R.drawable.watched_screen)
    val animeList = WatchedAnimeStore.watchedAnimeList
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds
        )
        AnimatedBorderCard(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .padding(top = 20.dp),
            shape = RoundedCornerShape(4.dp),
            borderWidth = 3.dp,
            gradient = Brush.linearGradient(
                listOf(
                    Color(android.graphics.Color.rgb(117, 27, 16)),
                    Color(android.graphics.Color.rgb(219, 136, 81))
                )
            )
        ) {
            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = "Here you can see all your animes watched",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                animeList.forEach { anime ->
                    AnimeItem(anime = anime)
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Gray)
            )
        }
    }
}


object WatchedAnimeStore {
    private val _watchedAnimeList = mutableStateListOf<Data>()
    val watchedAnimeList: List<Data> = _watchedAnimeList

    fun addAnime(anime: Data) {
        if (!_watchedAnimeList.contains(anime)) {
            _watchedAnimeList.add(anime)
        }
    }
}
