package com.guga.asunaanimes.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Brand gradient shared by every bordered card and dialog in the app. */
val AsunaGradientStart = Color(red = 117, green = 27, blue = 16)
val AsunaGradientEnd = Color(red = 219, green = 136, blue = 81)
val AsunaGradient: Brush = Brush.linearGradient(listOf(AsunaGradientStart, AsunaGradientEnd))

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val lightBlue = Color(0xFFADD8E6) // lightBlue
val darkBlue = Color(0xFF00008B)   // darkBlue