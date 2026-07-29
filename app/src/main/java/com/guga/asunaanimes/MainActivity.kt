package com.guga.asunaanimes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.guga.asunaanimes.presentation.navigation.AsunaNavGraph
import com.guga.asunaanimes.presentation.theme.MyAnimeListTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Must extend [AppCompatActivity] so [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]
 * can recreate the activity and refresh Compose `stringResource` values.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                AsunaNavGraph(navController = rememberNavController())
            }
        }
    }
}
