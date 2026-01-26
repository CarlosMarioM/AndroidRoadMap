package com.example.androidroadmap.features.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.home.ui.HomeScreen

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    HomeScreen()
                }
            }
        )
    }
}