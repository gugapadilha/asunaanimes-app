package com.guga.asunaanimes.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AsunaDarkColorScheme = darkColorScheme(
    primary = AsunaOrange,
    onPrimary = AsunaBlack,
    primaryContainer = AsunaOrangeDark,
    onPrimaryContainer = AsunaOnSurface,
    secondary = AsunaOrangeSoft,
    onSecondary = AsunaBlack,
    tertiary = AsunaScore,
    background = AsunaBlack,
    onBackground = AsunaOnSurface,
    surface = AsunaSurface,
    onSurface = AsunaOnSurface,
    surfaceVariant = AsunaSurfaceElevated,
    onSurfaceVariant = AsunaOnSurfaceMuted,
    outline = AsunaOrangeDark,
    error = Color(0xFFFF6B6B),
    onError = AsunaBlack
)

@Composable
fun MyAnimeListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Brand identity is always the dark orange/black palette, regardless of system theme.
    val colorScheme = AsunaDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
