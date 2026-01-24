package com.example.androidroadmap.features.landing

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidroadmap.features.index.IndexActivity


@Composable
fun LandingScreen(
    appName: String,
) {
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = appName,
                    style = MaterialTheme.typography.displayMedium
                )
            }

            // Body
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ready to begin your Android journey?",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // CTA
            Button(
                onClick = {
                    context.startActivity(Intent(context, IndexActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start learning")
            }
        }
    }
}