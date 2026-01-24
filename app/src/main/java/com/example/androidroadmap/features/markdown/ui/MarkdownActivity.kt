package com.example.androidroadmap.features.markdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.markdown.ui.MarkdownScreen

class MarkdownActivity(private val path : String) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ComposeView(this).apply {
                setContent {

                }
            }
        )
    }
}