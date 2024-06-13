package com.example.myanimelist.presentation.theme.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.myanimelist.data.repository.AnimeRepository
import com.example.myanimelist.domain.model.Anime

@Composable
fun CustomItem(anime: Anime) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .width(140.dp)
                .height(100.dp),
            painter = rememberAsyncImagePainter(anime.photoUrl),
            contentDescription = "Anime Screen",
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
fun CustomFavorite(anime: Anime, position: Int) {
    val defaultColor = Color.White
    val textColor = when (position) {
        0 -> Color(0xFFFFD700)
        1 -> Color(0xFFC0C0C0)
        2 -> Color(0xFFCD7F32)
        else -> defaultColor
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = "${anime.order}°",
            color = textColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Light
        )
        Image(
            painter = rememberImagePainter(data = anime.photoUrl, builder = {
            }),
            contentDescription = "Anime picture",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
        Text(
            text = anime.name,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Spacer(modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.Gray)
        )
    }

}