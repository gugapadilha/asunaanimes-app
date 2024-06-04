package com.example.myanimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myanimelist.nav.SetupNavGraph
import com.example.myanimelist.presentation.InitialScreen
import com.example.myanimelist.ui.theme.MyAnimeListTheme

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}