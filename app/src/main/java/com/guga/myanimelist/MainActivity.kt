package com.guga.myanimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.guga.myanimelist.nav.SetupNavGraph
import com.guga.myanimelist.presentation.theme.MyAnimeListTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        delay(1000)
                        navController.navigate("home_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    }
                }

                SetupNavGraph(navController = navController)
            }
        }
    }
}