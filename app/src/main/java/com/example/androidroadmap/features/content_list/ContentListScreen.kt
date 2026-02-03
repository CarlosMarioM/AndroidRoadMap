package com.example.androidroadmap.features.content_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidroadmap.ui.composables.CardItem
import com.example.androidroadmap.ui.composables.CenteredError
import com.example.androidroadmap.ui.composables.CenteredLoader
import com.example.androidroadmap.ui.composables.Header
import com.example.androidroadmap.ui.composables.SearchBar
import com.example.androidroadmap.ui.BackgroundDark

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

