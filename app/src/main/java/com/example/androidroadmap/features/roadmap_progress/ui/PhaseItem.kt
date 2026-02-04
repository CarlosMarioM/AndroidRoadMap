package com.example.androidroadmap.features.roadmap_progress.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.androidroadmap.domain.model.topic.Phase
import com.example.androidroadmap.ui.CardSurface
import com.example.androidroadmap.ui.TextPrimary

@Composable
fun PhaseItem(
    phase: Phase,
    onSubtopicClick: (String) -> Unit,
    onSubtopicToggleCompletion: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Text(
                    text = "${phase.order}. ${phase.title}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    color = TextPrimary
                )

                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextPrimary,
                )
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))

                phase.topics.forEach { topic ->
                    TopicItem(
                        topic = topic,
                        onSubtopicClick = onSubtopicClick,
                        onSubtopicToggleCompletion = onSubtopicToggleCompletion
                    )
                }
            }
        }
    }
}
