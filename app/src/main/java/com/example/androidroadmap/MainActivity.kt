package com.example.androidroadmap

import RoadMapAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidroadmap.features.landing.LandingScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoadMapAppTheme (view = {
                LandingScreen(
                    appName = "AndroidRoadMap.kt",
                )
            })
        }
    }
}
