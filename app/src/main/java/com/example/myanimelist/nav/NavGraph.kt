package com.example.myanimelist.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myanimelist.presentation.InitialScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Initial.route){
        composable(
            route = Screen.Initial.route
        ) {
            InitialScreen(navController)
        }
    }
    
}
