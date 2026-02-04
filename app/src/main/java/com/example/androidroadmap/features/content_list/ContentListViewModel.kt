package com.example.androidroadmap.features.content_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.data.topics.utils.TopicsMarkdownUtil
import com.example.androidroadmap.domain.repository.topics.usecases.GetRoadmapPhasesUseCase
import com.example.androidroadmap.domain.model.topic.Phase
import com.example.androidroadmap.domain.model.topic.Subtopic
import com.example.androidroadmap.domain.model.topic.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentListViewModel @Inject constructor(
    private val roadmapUseCase: GetRoadmapPhasesUseCase,
    private val topicsUtil: TopicsMarkdownUtil,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val args = ContentArgs(
        level = ContentLevel.valueOf(savedStateHandle["level"]!!),
        parentId = savedStateHandle["parentId"],
        title = savedStateHandle["title"]!!
    )

    private val _uiState = MutableStateFlow<ContentUiState>(ContentUiState.Loading)
    val uiState: StateFlow<ContentUiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching {
                when (args.level) {
                    ContentLevel.PHASES -> roadmapUseCase().map { PhaseItem(it) }
                    ContentLevel.TOPICS -> topicsUtil.readTopics()
                        .phases.first { it.id == args.parentId }
                        .topics.map { TopicItem(it) }
                    ContentLevel.SUBTOPICS -> topicsUtil.readTopics()
                        .phases.flatMap { it.topics }
                        .first { it.id == args.parentId }
                        .subtopics.map { SubtopicItem(it) }
                }
            }.onSuccess { items ->
                _uiState.value = ContentUiState.Success(items)
            }.onFailure {
                _uiState.value = ContentUiState.Error(it.message ?: "Unknown error")
            }
        }
    }
}

enum class ContentLevel {
    PHASES,
    TOPICS,
    SUBTOPICS
}

data class ContentArgs(
    val level: ContentLevel,
    val parentId: String? = null,
    val title: String
)

sealed interface ContentUiState {
    data object Loading : ContentUiState
    data class Success(val list: List<ContentListItem>) : ContentUiState
    data class Error(val error : String) : ContentUiState
}

sealed interface ContentListItem {
    val id: String
    val title: String
}

data class PhaseItem(val phase: Phase) : ContentListItem {
    override val id = phase.id
    override val title = phase.id
}

data class TopicItem(val topic: Topic) : ContentListItem {
    override val id = topic.id
    override val title = topic.title
}

data class SubtopicItem(val subtopic: Subtopic) : ContentListItem {
    override val id = subtopic.id
    override val title = subtopic.title
}
