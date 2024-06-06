package com.example.myanimelist.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.myanimelist.data.model.Anime
import com.example.myanimelist.data.repository.AnimeRepository

@Composable

fun CustomItem() {
    val anime = Anime(name = "Nome do Anime", description = "Descrição do Anime", photoUrl = "url_da_foto")

    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = rememberImagePainter(data = anime.photoUrl),
            contentDescription = "Anime Screen",
            contentScale = ContentScale.FillBounds
        )
    }

}