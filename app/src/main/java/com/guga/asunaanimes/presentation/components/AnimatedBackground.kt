package com.guga.asunaanimes.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R

/**
 * Full-screen background with a subtle Ken Burns drift so the artwork feels alive without
 * distracting from the content above it.
 */
@Composable
fun AnimatedBackground(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    overlayAlpha: Float = 0.28f
) {
    val transition = rememberInfiniteTransition(label = "ken_burns")
    val scale by transition.animateFloat(
        initialValue = 1.08f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_scale"
    )
    val offsetX by transition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_x"
    )
    val offsetY by transition.animateFloat(
        initialValue = -10f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 26_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_y"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(imageRes),
            contentDescription = stringResource(R.string.content_description_background_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = overlayAlpha * 0.7f),
                            Color.Black.copy(alpha = overlayAlpha),
                            Color.Black.copy(alpha = (overlayAlpha + 0.22f).coerceAtMost(0.72f))
                        )
                    )
                )
        )
    }
}
