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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.data.repository.YearRepository
import com.example.myanimelist.presentation.util.CustomItem
import com.example.myanimelist.presentation.util.CustomWatched

@Composable
fun WatchedScreen(navController: NavHostController) {
    val painter = rememberAsyncImagePainter(R.drawable.watched_screen)

    val animeRepository = AnimeRepository()
    val animeList = animeRepository.getAllData()

    val yearRepository = YearRepository()
    val yearList = yearRepository.getAllYears()


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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            yearList.forEach { year ->
                Text(
                    text = year.year.toString(),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    animeList.forEach { anime ->
                        CustomWatched(anime = anime)
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
}
