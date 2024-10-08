package com.guga.myanimelist.presentation.ui.bottomsheet

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.guga.myanimelist.domain.model.Data
import com.guga.myanimelist.presentation.ui.AnimatedBorderCard
import com.guga.myanimelist.presentation.util.preferences.FavoriteAnimeStore
import com.guga.myanimelist.presentation.util.preferences.WatchedAnimeStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import com.guga.myanimelist.R

@Composable
fun RemoveAnimeBottomSheet(
    anime: Data,
    onDismiss: () -> Unit,
    removeFromFavorite: Boolean,
    animeList: MutableList<Data>? = null
) {
    val painter = rememberAsyncImagePainter(R.drawable.bottomsheet_screen)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun removeAnime() {
        coroutineScope.launch {
            if (removeFromFavorite) {
                FavoriteAnimeStore.removeAnime(anime, context)
                Toast.makeText(context, "Anime Removed from Favorites!", Toast.LENGTH_SHORT).show()
                animeList?.remove(anime)
            } else {
                WatchedAnimeStore.removeAnime(anime, context)
                Toast.makeText(context, "Anime Removed from Watched!", Toast.LENGTH_SHORT).show()
                animeList?.remove(anime)
            }
            onDismiss()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberImagePainter(data = anime.images.jpg.imageUrl ?: ""),
                    contentDescription = "Anime picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .width(130.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = anime.title ?: "Title not available",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                    Text(
                        text = "Score: ${anime.score ?: "N/A"}",
                        color = Color.Yellow,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (anime.aired?.to.isNullOrEmpty()) {
                            "${anime.aired?.from?.let { formatDate(it) } ?: "Unknown start date"} - ongoing"
                        } else {
                            "${anime.aired.from?.let { formatDate(it) } ?: "Unknown start date"} - ${anime.aired.to?.let { formatDate(it) } ?: "Unknown end date"}"
                        },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "${anime.episodes ?: "Unknown"} episodes",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Rating: ${anime.rating ?: "N/A"}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Know more",
                        color = Color.Cyan,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clickable {
                                val url = anime.url ?: "https://myanimelist.net/"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                    )
                    AnimeCard(onRemoveClick = { removeAnime() })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = anime.synopsis ?: "Synopsis not available.",
                color = Color.White,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
    }
}

private fun formatDate(date: String): String {
    return try {
        if (date.contains("-")) {
            val newDate = date.substring(0, date.lastIndexOf("-"))
            val _date = SimpleDateFormat("yyyy-MM").parse(newDate)
            SimpleDateFormat("MMM-yyyy").format(_date)
        } else {
            date
        }
    } catch (e: Exception) {
        "Unknown date"
    }
}

@Composable
private fun AnimeCard(onRemoveClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(45.dp)
            .padding(top = 10.dp)
    ) {
        AnimatedBorderCard(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            borderWidth = 3.dp,
            gradient = Brush.linearGradient(
                listOf(
                    Color(android.graphics.Color.rgb(117, 27, 16)),
                    Color(android.graphics.Color.rgb(219, 136, 81))
                )
            ),
            onCardClick = { onRemoveClick() }
        ) {
            androidx.compose.material3.Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp),
                text = "Remove Anime",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
