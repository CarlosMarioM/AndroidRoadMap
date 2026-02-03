package com.example.androidroadmap

import RoadMapAppTheme
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.androidroadmap.features.landing.LandingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            RoadMapAppTheme {
                LandingScreen(
                    appName = "AndroidRoadMap.kt",
                )
            }
        }
    }
}
