package com.example.myanimelist.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myanimelist.ui.HomeScreen
import com.example.myanimelist.ui.InitialScreen
import com.example.myanimelist.ui.WatchedScreen

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
        composable(route = Screen.Watched.route){
            WatchedScreen(navController = navController)
        }
        composable(route = Screen.Favorite.route){
            WatchedScreen(navController = navController)
        }
    }
    
}
