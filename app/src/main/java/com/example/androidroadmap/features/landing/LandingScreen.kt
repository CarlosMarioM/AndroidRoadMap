package com.example.androidroadmap.features.landing

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.features.home.HomeActivity
import com.example.androidroadmap.ui.theme.BackgroundDark
import com.example.androidroadmap.ui.theme.TextPrimary
import com.example.androidroadmap.ui.theme.TextSecondary


@Composable
fun LandingScreen(
    appName: String,
) {
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark) // Dark Background
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceAround
        ) {

            // Header
            Column {
                Text(
                    text = "Welcome to",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = appName,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(84.dp))

            // Body
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ready to begin your Android journey?",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Thin,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(84.dp))

            // CTA
            Button(
                onClick = {
                    context.startActivity(Intent(context, HomeActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(73.dp)
            ) {
                Text(
                    "Start learning",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin,
                    fontFamily = FontFamily.Monospace
                    )
            }
        }
    }
}