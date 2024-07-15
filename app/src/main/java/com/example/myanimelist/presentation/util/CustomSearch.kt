package com.example.myanimelist.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.domain.model.Anime
import com.example.myanimelist.presentation.ui.AnimatedBorderCard

@Composable
fun SearchBox() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 10.dp, end = 10.dp),

            ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(text = "eg: Fate Zero") },
                singleLine = true,
            )

            AnimeCard()
        }

    }
}

@Composable
fun AnimeItem(anime: Anime) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = anime.photoUrl),
            contentDescription = "Anime picture",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Text(
            text = anime.name,
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 40.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnimeCard() {
    Box(
        modifier = Modifier
            .height(50.dp)
    ) {
        AnimatedBorderCard(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            borderWidth = 3.dp,
            gradient = Brush.linearGradient(
                listOf(
                    Color(android.graphics.Color.rgb(117, 27, 16)),
                    Color(
                        android.graphics.Color.rgb(219, 136, 81)
                    )
                )
            ),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                text = "Search",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
fun PreviewCustomSearch() {
    val animeRepository = AnimeRepository()
    val getAllAnimeData = animeRepository.getAllData()

    SearchBox()
    AnimeItem(anime = getAllAnimeData.first())
}