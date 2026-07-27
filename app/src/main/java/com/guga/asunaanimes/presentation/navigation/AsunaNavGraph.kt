package com.guga.asunaanimes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guga.asunaanimes.presentation.collection.FavoriteScreen
import com.guga.asunaanimes.presentation.collection.WatchedScreen
import com.guga.asunaanimes.presentation.home.HomeScreen
import com.guga.asunaanimes.presentation.search.SearchScreen
import com.guga.asunaanimes.presentation.splash.SplashScreen

@Composable
fun AsunaNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeScreen(onDestinationClick = { screen -> navController.navigateOnce(screen) })
        }
        composable(route = Screen.Search.route) {
            SearchScreen()
        }
        composable(route = Screen.Watched.route) {
            WatchedScreen()
        }
        composable(route = Screen.Favorite.route) {
            FavoriteScreen()
        }
    }
}

/**
 * Guards against the duplicated destinations a fast double tap used to push onto the back stack.
 */
private fun NavHostController.navigateOnce(screen: Screen) {
    navigate(screen.route) { launchSingleTop = true }
}
