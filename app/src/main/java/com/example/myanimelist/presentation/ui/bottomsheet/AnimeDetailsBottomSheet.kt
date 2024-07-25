package com.example.myanimelist.presentation.ui.bottomsheet

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.myanimelist.R
import com.example.myanimelist.domain.model2.Data
import java.text.SimpleDateFormat

@Composable
fun AnimeDetailsBottomSheet(anime: Data) {

    val painter = rememberAsyncImagePainter(R.drawable.bottomsheet_screen)

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
                    painter = rememberImagePainter(data = anime.images.jpg.imageUrl),
                    contentDescription = "Anime picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .width(130.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = anime.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Score: ${anime.score}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (anime.aired.to.isEmpty()) {
                            "${formatDate(anime.aired.from)} - ongoing"
                        } else {
                            "${formatDate(anime.aired.from)} - ${formatDate(anime.aired.to)}"
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "${anime.episodes} episodes",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Rating: ${anime.rating}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val context = LocalContext.current
                    Text(
                        text = "Know more",
                        color = Color.Cyan,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(anime.url))
                                context.startActivity(intent)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = anime.synopsis,
                color = Color.White,
                fontSize = 14.sp,
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
    return if (date.contains("-")) {
        val newDate = date.substring(0, date.lastIndexOf("-"))
        val _date = SimpleDateFormat("yyyy-MM").parse(newDate)
        SimpleDateFormat("MMM-yyyy").format(_date)
    } else {
        date
    }
}
