package com.example.myanimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myanimelist.presentation.InitialScreen
import com.example.myanimelist.ui.theme.MyAnimeListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimeListTheme {
                InitialScreen()
            }
        }
    }
}