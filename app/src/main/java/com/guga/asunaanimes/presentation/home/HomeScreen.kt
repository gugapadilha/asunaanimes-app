package com.guga.asunaanimes.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.presentation.components.GradientBorderButton
import com.guga.asunaanimes.presentation.navigation.Screen

@Composable
fun HomeScreen(onDestinationClick: (Screen) -> Unit) {
    val painter = rememberAsyncImagePainter(R.drawable.home_screen)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = stringResource(R.string.content_description_anime_screen),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.asuna_animes),
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
        Spacer(modifier = Modifier.weight(1f))
        HomeShortcut(
            text = stringResource(R.string.home_search_animes),
            onClick = { onDestinationClick(Screen.Search) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HomeShortcut(
            text = stringResource(R.string.home_watched_animes),
            onClick = { onDestinationClick(Screen.Watched) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HomeShortcut(
            text = stringResource(R.string.home_favorite_animes),
            onClick = { onDestinationClick(Screen.Favorite) }
        )
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun HomeShortcut(text: String, onClick: () -> Unit) {
    GradientBorderButton(
        text = text,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        fontSize = 26.sp,
        textTopPadding = 13.dp
    )
}
