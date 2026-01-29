package com.example.androidroadmap.features.content_list

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.features.markdown.MarkdownActivity

@Composable
fun ContentListRoute(
    viewModel: ContentListViewModel = viewModel(),
    onNavigate: (ContentArgs) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val (subtitle, searchHint) = when (viewModel.args.level) {
        ContentLevel.PHASES -> "Project Structure & Topics" to "Search phases..."
        ContentLevel.TOPICS -> "Topics" to "Search topics..."
        ContentLevel.SUBTOPICS -> "Subtopics" to "Search subtopics..."
    }

    ContentListScreen(
        title = viewModel.args.title,
        subtitle = subtitle,
        searchHint = searchHint,
        uiState = uiState,
        onItemClick = { item ->
            when (item) {
                is PhaseItem -> onNavigate(ContentArgs(ContentLevel.TOPICS, item.id, item.title))
                is TopicItem -> onNavigate(ContentArgs(ContentLevel.SUBTOPICS, item.id, item.title))
                is SubtopicItem -> {
                    val intent = Intent(context, MarkdownActivity::class.java).apply {
                        putExtra("SUBTOPIC_ID", item.id)
                    }
                    context.startActivity(intent)
                }

            }
        }
    )
}
