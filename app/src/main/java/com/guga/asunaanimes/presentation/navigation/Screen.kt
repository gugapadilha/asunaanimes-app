package com.guga.asunaanimes.presentation.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen(route = "splash_screen")
    data object Home : Screen(route = "home_screen")
    data object Search : Screen(route = "search_screen")
    data object Watched : Screen(route = "watched_screen")
    data object Favorite : Screen(route = "favorite_screen")
}
