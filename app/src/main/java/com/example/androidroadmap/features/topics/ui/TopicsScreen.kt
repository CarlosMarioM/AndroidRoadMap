package com.example.androidroadmap.features.topics.ui

import com.example.androidroadmap.model.Topic
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TopicsScreen(
    topics: List<Topic>,
    onClick: (Topic) -> Unit
) {
    LazyColumn {
        items(topics.size) { index ->
            ListItem(
                headlineContent = { Text(topics[index].title) },
                modifier = Modifier.clickable { onClick(topics[index]) }
            )
        }
    }
}
