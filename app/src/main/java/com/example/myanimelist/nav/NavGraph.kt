package com.example.myanimelist.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun SetupNavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.Home.route){

    }
    
}
