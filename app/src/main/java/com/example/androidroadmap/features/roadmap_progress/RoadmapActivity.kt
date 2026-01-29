package com.example.androidroadmap.features.roadmap_progress

import RoadMapAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.roadmap_progress.ui.RoadmapScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class RoadmapActivity : ComponentActivity() {
    private val viewModel: RoadmapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
               setContent {
                   RoadmapScreen()
               }
            }
        )
    }

}
