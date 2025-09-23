package com.guga.asunaanimes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.guga.asunaanimes.nav.SetupNavGraph
import com.guga.asunaanimes.presentation.theme.MyAnimeListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                val navController = rememberNavController()

                SetupNavGraph(navController = navController)
            }
        }
    }
}