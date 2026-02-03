package com.example.androidroadmap.features.roadmap_progress.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.features.markdown.MarkdownActivity
import com.example.androidroadmap.features.roadmap_progress.RoadmapUiState
import com.example.androidroadmap.features.roadmap_progress.RoadmapViewModel
import com.example.androidroadmap.ui.composables.CenteredError
import com.example.androidroadmap.ui.composables.CenteredLoader
import com.example.androidroadmap.ui.composables.Header
import com.example.androidroadmap.ui.BackgroundDark

@Composable
fun RoadmapScreen(
    modifier: Modifier = Modifier,
    viewModel: RoadmapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val onSubtopicClick: (String) -> Unit = { subtopicId ->
        context.startActivity(
            Intent(context, MarkdownActivity::class.java)
                .putExtra("SUBTOPIC_ID", subtopicId)
        )
    }

    Scaffold(
        modifier = modifier.background(BackgroundDark)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .background(BackgroundDark)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Header(title = "Roadmap.kt", subtitle = "Progress")
            when (uiState) {
                is RoadmapUiState.Loading -> CenteredLoader()
                is RoadmapUiState.Error -> CenteredError((uiState as RoadmapUiState.Error).error)
                is RoadmapUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 12.dp)

                    ) {
                        items((uiState as RoadmapUiState.Success).phases) { phase ->
                            PhaseItem(
                                phase = phase,
                                onSubtopicClick = onSubtopicClick,
                                onSubtopicToggleCompletion = viewModel::onSubtopicToggleCompletion
                            )
                        }
                    }
                }
            }
        }
    }
}
