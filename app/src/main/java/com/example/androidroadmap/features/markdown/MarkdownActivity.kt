package com.example.androidroadmap.features.markdown  

import MarkdownScreen
import RoadMapAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MarkdownActivity : ComponentActivity() {
    private val viewModel: MarkdownViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val subtopicId = intent.getStringExtra("SUBTOPIC_ID") ?: return


        setContent {
            RoadMapAppTheme {
                MarkdownScreen(
                    viewModel = viewModel,
                    subtopicId = subtopicId,
                    onScrolledToEnd = { viewModel.onScrolledToEnd() }
                )
            }
        }
    }
}