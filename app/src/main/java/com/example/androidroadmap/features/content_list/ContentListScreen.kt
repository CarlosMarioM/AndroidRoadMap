package com.example.androidroadmap.features.content_list

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.model.Phase
import com.example.androidroadmap.model.Subtopic
import com.example.androidroadmap.model.Topic
import com.example.androidroadmap.ui.CardItem
import com.example.androidroadmap.ui.CenteredError
import com.example.androidroadmap.ui.CenteredLoader
import com.example.androidroadmap.ui.Header
import com.example.androidroadmap.ui.SearchBar
import com.example.androidroadmap.ui.theme.BackgroundDark
import com.example.androidroadmap.ui.theme.TextPrimary
import com.example.androidroadmap.ui.theme.TextSecondary
@Composable
fun ContentListScreen(
    title: String,
    subtitle: String,
    searchHint: String,
    uiState: ContentUiState,
    onItemClick: (ContentListItem) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(16.dp)
                .padding(innerPadding)
        ) {

            Header(title, subtitle)
            SearchBar(searchHint)

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState) {
                ContentUiState.Loading -> CenteredLoader()
                is ContentUiState.Error -> CenteredError(uiState.error)
                is ContentUiState.Success -> ContentList(uiState.list, onItemClick)
            }
        }
    }
}

@Composable
private fun ContentList(
    items: List<ContentListItem>,
    onItemClick: (ContentListItem) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(items) { index, item ->
            CardItem(
                index = index,
                title = when (item) {
                    is PhaseItem -> item.id
                    is TopicItem -> item.title
                    is SubtopicItem -> item.title
                },
                onClick = { onItemClick(item) }
            )
        }
    }
}

