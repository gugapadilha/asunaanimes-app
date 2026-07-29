package com.guga.asunaanimes.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * MAL-inspired personal score colors: 10 blue, 9 green, then yellow → orange → red.
 */
fun userScoreColor(score: Int): Color = when (score) {
    10 -> Color(0xFF42A5F5)
    9 -> Color(0xFF66BB6A)
    8 -> Color(0xFF9CCC65)
    7 -> Color(0xFFD4E157)
    6 -> Color(0xFFFFEE58)
    5 -> Color(0xFFFFCA28)
    4 -> Color(0xFFFFA726)
    3 -> Color(0xFFEF5350)
    2 -> Color(0xFFE53935)
    1 -> Color(0xFFAB47BC)
    else -> AsunaOrange
}

fun userScoreContentColor(score: Int): Color =
    if (score <= 3) Color.White else Color(0xFF121212)
