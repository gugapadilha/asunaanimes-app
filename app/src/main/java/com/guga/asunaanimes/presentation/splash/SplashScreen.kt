package com.guga.asunaanimes.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guga.asunaanimes.R
import com.guga.asunaanimes.presentation.components.AnimatedBackground
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaOrangeSoft
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 2_200L

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val currentOnSplashFinished by rememberUpdatedState(onSplashFinished)
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "splash_fade"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(SPLASH_DURATION_MS)
        currentOnSplashFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(
            imageRes = R.drawable.splashscreen,
            overlayAlpha = 0.35f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .alpha(alpha),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(96.dp))
            Text(
                text = stringResource(R.string.asuna_animes),
                color = AsunaOnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp),
                color = AsunaOrange,
                trackColor = AsunaOrangeSoft.copy(alpha = 0.25f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}
