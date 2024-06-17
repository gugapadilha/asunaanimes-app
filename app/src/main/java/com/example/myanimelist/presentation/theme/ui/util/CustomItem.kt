package com.example.myanimelist.presentation.theme.ui.util

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
import com.example.myanimelist.data.repository.AnimeRepository

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

        Spacer(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Gray)
        )
    }

}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CustomBest() {

    val animeRepository = AnimeRepository()
    val getAllAnimeData = animeRepository.getAllData()

    Orbital {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(
                items = getAllAnimeData,
                key = { it.name }
            ) { anime ->
                var expanded by rememberSaveable { mutableStateOf(false) }
                AnimatedVisibility(
                    remember {
                        MutableTransitionState(false)
                    }
                        .apply { targetState = true },
                    enter = fadeIn(tween(durationMillis = 300)),
                    exit = fadeOut(tween(durationMillis = 300))
                ) {
                    Orbital(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickableWithoutRipple(
                                interactionSource = MutableInteractionSource(),
                                onClick = { expanded = !expanded }
                            )
                    ) {
                        val text = rememberMovableContentOf {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .padding(horizontal = if (expanded) 20.dp else 10.dp)
                                    .animateBounds(
                                        sizeAnimationSpec = tween(durationMillis = 300),
                                        positionAnimationSpec = tween(durationMillis = 300)
                                    )
                            ) {
                                Text(
                                    text = anime.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = if (expanded) 2 else 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = anime.desc,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = if (expanded) 10 else 2,
                                    overflow = TextOverflow.Clip
                                )
                            }
                        }
                        val image = rememberMovableContentOf {
                            AsyncImage(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .animateBounds(
                                        modifier = if (expanded) {
                                            Modifier.fillMaxWidth()
                                        } else {
                                            Modifier.size(100.dp)
                                        },
                                        sizeAnimationSpec = tween(durationMillis = 300),
                                        positionAnimationSpec = tween(durationMillis = 300),
                                    )
                                    .clip(RoundedCornerShape(10.dp)),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(anime.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Mountain Image",
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (expanded) {
                            Column(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .clip(RoundedCornerShape(size = 10.dp))
                                    .background(
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                                            elevation = 10.dp
                                        )
                                    )
                            ) {
                                image()
                                text()
                            }
                        } else {
                            Row {
                                image()
                                text()
                            }
                        }
                    }
                }
            }
        }

    }
}

fun Modifier.clickableWithoutRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) = composed(
    factory = {
        this.then(
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick() }
            )
        )
    }
)