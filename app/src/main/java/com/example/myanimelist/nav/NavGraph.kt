package com.example.myanimelist.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myanimelist.presentation.ui.BestScreen
import com.example.myanimelist.presentation.ui.FavoriteScreen
import com.example.myanimelist.presentation.ui.HomeScreen
import com.example.myanimelist.presentation.ui.InitialScreen
import com.example.myanimelist.presentation.ui.SearchScreen
import com.example.myanimelist.presentation.ui.WatchedScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Initial.route){

        composable(route = Screen.Initial.route) {
            InitialScreen(navController)
        }
        composable(route = Screen.Home.route){
            HomeScreen(navController = navController)
        }
        composable(route = Screen.Search.route){
            SearchScreen(navController = navController)
        }
        composable(route = Screen.Watched.route){
            WatchedScreen(navController = navController)
        }
        composable(route = Screen.Favorite.route){
            FavoriteScreen(navController = navController)
        }
        composable(route = Screen.Best.route){
            BestScreen(navController = navController)
        }
    }
}
