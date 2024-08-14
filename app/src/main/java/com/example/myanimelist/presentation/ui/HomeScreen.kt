package com.example.myanimelist.presentation.ui

import android.graphics.Color.rgb
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R

@Composable
fun HomeScreen(navController: NavController) {

    val painter = rememberAsyncImagePainter(R.drawable.home_screen)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Anime Screen",
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 100.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ASUNA ANIMES",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(250.dp)
                    .height(1.dp)
                    .background(Color.White)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(630.dp))
        AnimeCard(navController = navController, "Search Animes", route = "search_screen")
        Spacer(modifier = Modifier.height(16.dp))
        AnimeCard(navController = navController, "Watched Animes", route = "watched_screen")
        Spacer(modifier = Modifier.height(16.dp))
        AnimeCard(navController = navController, "Favorite Animes", route = "favorite_screen")
    }
}

@Composable
fun AnimeCard(navController: NavController, textUnit: String, route: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        AnimatedBorderCard(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            borderWidth = 3.dp,
            gradient = Brush.linearGradient(
                listOf(
                    Color(rgb(117, 27, 16)),
                    Color(rgb(219, 136, 81)
                ))
            ),
            onCardClick = { navController.navigate(route = route) }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { navController.navigate(route = route) }
                    .padding(top = 13.dp),
                text = textUnit,
                color = Color.White,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}