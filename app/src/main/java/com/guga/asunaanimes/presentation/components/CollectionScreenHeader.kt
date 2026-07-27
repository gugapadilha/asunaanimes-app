package com.guga.asunaanimes.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaOrangeDark
import com.guga.asunaanimes.presentation.theme.AsunaOrangeSoft

@Composable
fun CollectionScreenHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val pulse = rememberInfiniteTransition(label = "header_pulse")
    val underlineAlpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "header_underline_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = AsunaOnSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .alpha(underlineAlpha * 0.5f)
                    .background(AsunaOrangeSoft, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(3.dp)
                    .alpha(underlineAlpha)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AsunaOrangeDark, AsunaOrange, AsunaOrangeSoft)
                        ),
                        RoundedCornerShape(50)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Spacer(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .alpha(underlineAlpha * 0.5f)
                    .background(AsunaOrangeSoft, RoundedCornerShape(50))
            )
        }
    }
}
