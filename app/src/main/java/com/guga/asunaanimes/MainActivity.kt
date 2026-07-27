package com.guga.asunaanimes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.guga.asunaanimes.presentation.navigation.AsunaNavGraph
import com.guga.asunaanimes.presentation.theme.MyAnimeListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                AsunaNavGraph(navController = rememberNavController())
            }
        }
    }
}
