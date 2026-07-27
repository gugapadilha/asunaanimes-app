package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

/**
 * The bordered, gradient-animated button reused by the home shortcuts, the search action and the
 * anime detail sheets.
 */
@Composable
fun GradientBorderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    textTopPadding: Dp = 10.dp
) {
    Box(modifier = modifier) {
        AnimatedBorderCard(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            borderWidth = 3.dp,
            onCardClick = onClick
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = textTopPadding),
                text = text,
                color = Color.White,
                fontSize = fontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}
