package com.guga.asunaanimes.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guga.asunaanimes.presentation.splash.SplashScreen

private const val ROOT_ANIMATION_MS = 450

@Composable
fun AsunaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(animationSpec = tween(ROOT_ANIMATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(ROOT_ANIMATION_MS)) }
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screen.Main.route) {
            MainScaffold()
        }
    }
}
