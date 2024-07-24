package com.example.myanimelist.presentation.ui.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.myanimelist.domain.model2.Data
import java.text.SimpleDateFormat

@Composable
fun AnimeDetailsBottomSheet(anime: Data) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = rememberImagePainter(data = anime.images.jpg.imageUrl),
                contentDescription = "Anime picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .width(130.dp)
            )
            Text(
                text = anime.title,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
            Text(
                text = anime.score.toString(),
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
            Text(
                text = anime.rating,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "${anime.episodes} episodes",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
            Text(
                text = anime.synopsis,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "${formatDate(anime.aired.from)}- ${formatDate(anime.aired.to)}",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
            )
        }
    }
}

private fun formatDate(date: String): String {
    return if (date.contains("-")){
        val newDate = date.substring(0, date.lastIndexOf("-"))
        val _date = SimpleDateFormat("yyyy-MM").parse(newDate)
        SimpleDateFormat("MMM-yyyy").format(_date)
    } else {
        date
    }
}
