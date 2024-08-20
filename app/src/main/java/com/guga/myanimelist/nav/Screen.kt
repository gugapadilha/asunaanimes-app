package com.guga.myanimelist.nav

sealed class Screen(val route: String) {

    object Initial: Screen(route = "initial_screen")
    object Home: Screen(route = "home_screen")
    object Search: Screen(route = "search_screen")
    object Watched: Screen(route = "watched_screen")
    object Favorite: Screen(route = "favorite_screen")
}