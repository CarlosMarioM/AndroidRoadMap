package com.example.androidroadmap.features.examples

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidroadmap.features.examples.weather_app.WeatherAppActivity
import com.example.androidroadmap.ui.CardItem
import com.example.androidroadmap.ui.Header
import com.example.androidroadmap.ui.theme.BackgroundDark

@Composable
fun ExamplesScreen() {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(16.dp)
                .padding(innerPadding)
        ) {

            Header(title = "Examples.kt", subtitle = "Real app examples")

            Spacer(modifier = Modifier.padding(vertical = 32.dp))

            CardItem(0, "WeatherApp", onClick = {
                val intent = Intent(context, WeatherAppActivity::class.java)
                context.startActivity(intent)
            })
        }
    }
}

