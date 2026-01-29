package com.example.androidroadmap

import RoadMapAppTheme
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidroadmap.features.landing.LandingScreen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application(){
}
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoadMapAppTheme {
                LandingScreen(
                    appName = "AndroidRoadMap.kt",
                )
            }
        }
    }
}
