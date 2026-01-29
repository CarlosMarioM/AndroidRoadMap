package com.example.androidroadmap.features.roadmap_progress.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.androidroadmap.model.Subtopic
import com.example.androidroadmap.ui.theme.OrangeAccent
import com.example.androidroadmap.ui.theme.TextPrimary
import com.example.androidroadmap.ui.theme.TextSecondary

@Composable
fun SubtopicItem(
    subtopic: Subtopic,
    onSubtopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    level: Int = 0
) {
    val indentation = level * 12.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indentation)
            .clickable { onSubtopicClick(subtopic.id) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subtopic.isCompleted,
            onCheckedChange = null ,
            colors = CheckboxDefaults.colors().copy(checkedBoxColor = OrangeAccent, checkedBorderColor = OrangeAccent)
        )

        Text(
            text = subtopic.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (subtopic.isCompleted)
                TextPrimary
            else
               TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}
