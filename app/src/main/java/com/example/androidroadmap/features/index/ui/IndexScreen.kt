package com.example.androidroadmap.features.index.ui

import android.content.Intent
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import com.example.androidroadmap.model.Topic
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.features.index.IndexActivity
import com.example.androidroadmap.features.markdown.MarkdownActivity
import com.example.androidroadmap.model.TopicId
import com.example.androidroadmap.theme.OrangeAccent
import com.example.androidroadmap.theme.PurpleAccent
import com.example.androidroadmap.theme.TealAccent

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
                .background(Color(0xFF1E1F22)) // Dark Background
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            // 1. Header Section
            Text(
                text = "Index.kt",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace // <--- The "Code" Look
            )
            Text(
                text = "Project Structure & Topics",
                color = Color(0xFF707277),
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
                    TopicCard(index, topic, onTopicClick )
                }
            }
        }
    }
}

@Composable
fun TopicCard(index : Int, topic: Topic,  onClick: (Topic) -> Unit) {
    // Cycle through colors based on index to get that colorful list
    val accentColor = when (index % 3) {
        1 -> PurpleAccent // Purple
        2 -> TealAccent // Teal
        else -> OrangeAccent// Orange
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Fixed height for consistency
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2B2D30))
            .clickable { onClick.invoke(topic) }, // Card Surface
        verticalAlignment = Alignment.CenterVertically
    ) {
        // A. The Colored "Glow" Strip on the left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.3f))
                    )
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        // B. The Line Number (01, 02, etc.)

        Text(
            text = String.format("%02d", index),
            color = Color(0xFF505257),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        VerticalDivider(
            color = Color.Gray,
            modifier = Modifier
            .height(60.dp))

        Spacer(modifier = Modifier.width(12.dp))
        // C. The Topic Title
        Text(
            text = topic.title,
            color = Color(0xFFBCBEC4),
            fontFamily = FontFamily.Monospace, // IMPORTANT for the look
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
