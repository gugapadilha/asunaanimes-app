package com.example.myanimelist.presentation.ui.bottomsheet

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.domain.model2.Data
import com.example.myanimelist.presentation.ui.AnimatedBorderCard
import com.example.myanimelist.presentation.ui.viewmodel.FavoriteAnimeStore
import com.example.myanimelist.presentation.ui.viewmodel.WatchedAnimeStore
import java.text.SimpleDateFormat

@Composable
fun AnimeDetailsBottomSheet(anime: Data) {
    val painter = rememberAsyncImagePainter(R.drawable.bottomsheet_screen)
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

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
                    AnimeCard(onSearchClick = { showDialog = true })
                    if (showDialog) {
                        AnimeOptionsDialog(
                            onDismiss = { showDialog = false },
                            anime = anime
                        )
                    }
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
private fun AnimeCard(onSearchClick: () -> Unit) {
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
                    Color(
                        android.graphics.Color.rgb(219, 136, 81)
                    ))
            ),
            onCardClick = { onSearchClick() }
        ) {
            androidx.compose.material3.Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp),
                text = "Add to your list",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnimeOptionsDialog(onDismiss: () -> Unit, anime: Data) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(android.graphics.Color.rgb(117, 27, 16)),
                            Color(android.graphics.Color.rgb(219, 136, 81))
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Favorite Animes",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(12.dp))
                        .clickable {
                            FavoriteAnimeStore.addAnime(anime, context)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Watched Animes",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(12.dp))
                        .clickable {
                            WatchedAnimeStore.addAnime(anime, context)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
    }
}