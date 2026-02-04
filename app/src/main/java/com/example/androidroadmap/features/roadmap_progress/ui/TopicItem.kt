package com.example.androidroadmap.features.roadmap_progress.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.androidroadmap.domain.model.topic.Topic
import com.example.androidroadmap.ui.TextSecondary

@Composable
fun TopicItem(
    topic: Topic,
    onSubtopicToggleCompletion: (String, Boolean) -> Unit,
    onSubtopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    level: Int = 0
) {
    var expanded by remember { mutableStateOf(false) }
    val indentation = level * 12.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indentation, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = topic.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = TextSecondary
            )

            if (topic.subtopics.isNotEmpty()) {
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        if (expanded) {
            topic.subtopics.forEach { subtopic ->
                SubtopicItem(
                    subtopic = subtopic,
                    onSubtopicClick = onSubtopicClick,
                    level = level + 1
                )
            }
        }
    }
}
