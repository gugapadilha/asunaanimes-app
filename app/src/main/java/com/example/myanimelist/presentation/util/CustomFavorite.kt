package com.example.myanimelist.presentation.util

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.myanimelist.domain.model.Anime
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.request.ImageRequest
import com.example.myanimelist.R
import com.example.myanimelist.data.repository.AnimeRepository


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

        Spacer(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Gray)
        )
    }

}