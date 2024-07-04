package com.example.myanimelist.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.myanimelist.presentation.util.CustomSearch

@Composable
fun SearchScreen(navController : NavHostController) {

    CustomSearch()
}