package com.example.androidroadmap.features.index.ui

import androidx.compose.foundation.background
import com.example.androidroadmap.model.Topic
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.model.TopicId
import com.example.androidroadmap.ui.theme.BackgroundDark
import com.example.androidroadmap.ui.theme.TextPrimary
import com.example.androidroadmap.ui.theme.TextSecondary
import com.example.androidroadmap.ui.card.CardItem

@Preview
@Composable
fun PreviewIndexScreen() {
    // Sample Data
    val topics = listOf(
        Topic(title = "Kotlin Syntax and Idioms", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),
        Topic(title = "Null Safety and Platform Types", id = TopicId(id = "Null Safety and Platform Types"), path = "Null Safety and Platform Types"),
        Topic(title = "Data Classes vs Sealed Classes", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),
        Topic(title = "Sealed Interfaces", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),
        Topic(title = "Inline Functions & Reified Generics", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),
        Topic(title = "Lambdas and Higher order functions", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),
        Topic(title = "Collections and Immutability", id = TopicId(id = "Kotlin Syntax and Idioms"), path = "Kotlin Syntax and Idioms"),

    )
    IndexScreen(topics = topics) { }
}

@Composable
fun IndexScreen(
    topics: List<Topic>,
    onTopicClick: (Topic) -> Unit
) {
    val context = LocalContext.current
    // Main Container
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark) // Dark Background
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            // 1. Header Section
            Text(
                text = "Index.kt",
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace // <--- The "Code" Look
            )
            Text(
                text = "Project Structure & Topics",
                color = TextSecondary,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 2. Search Bar (Visual only for now)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2B2D30))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search topics...", color = Color.Gray, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. The List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(topics) { index, topic ->
                    CardItem(index = index, title = topic.title, onClick = { onTopicClick(topic) })
                }
            }
        }
    }
}