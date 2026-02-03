package com.example.androidroadmap.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.ui.TextPrimary
import com.example.androidroadmap.ui.TextSecondary

@Composable
fun Header(title: String, subtitle: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}