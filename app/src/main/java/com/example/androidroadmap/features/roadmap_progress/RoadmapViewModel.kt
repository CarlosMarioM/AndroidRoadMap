package com.example.androidroadmap.features.roadmap_progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.topics.TopicProgress
import com.example.androidroadmap.domain.topics.usecases.GetAllTopicProgressUseCase
import com.example.androidroadmap.domain.topics.usecases.GetRoadmapPhasesUseCase
import com.example.androidroadmap.domain.topics.usecases.UpdateTopicProgressUseCase
import com.example.androidroadmap.model.Phase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val getRoadmapPhasesUseCase: GetRoadmapPhasesUseCase,
    private val updateTopicProgressUseCase: UpdateTopicProgressUseCase,
    private val getAllTopicProgressUseCase: GetAllTopicProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoadmapUiState>(RoadmapUiState.Loading)
    val uiState: StateFlow<RoadmapUiState> = _uiState

    // A separate StateFlow to hold the raw phases from the data source
    private val _phasesFromDataSource = MutableStateFlow<List<Phase>>(emptyList())

    init {
        loadRoadmap()
        observeTopicProgress()
    }

    private fun loadRoadmap() {
        viewModelScope.launch {
            try {
                val phases = getRoadmapPhasesUseCase()
                _phasesFromDataSource.value = phases
            } catch (e: Exception) {
                _uiState.value = RoadmapUiState.Error("Failed to load roadmap: ${e.message}")
            }
        }
    }

    private fun observeTopicProgress() {
        combine(
            _phasesFromDataSource,
            getAllTopicProgressUseCase()
        ) { phases, allProgress ->
            RoadmapUiState.Success(
                phases = phases.map { phase ->
                    phase.copy(
                        topics = phase.topics.map { topic ->
                            topic.copy(
                                subtopics = topic.subtopics.map { subtopic ->
                                    val progress = allProgress.find { it.subtopicId == subtopic.id }
                                    subtopic.copy(
                                        isCompleted = progress?.isCompleted ?: false,
                                        lastAccessedDate = progress?.lastAccessedDate,
                                        notes = progress?.notes
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }.onEach { uiState ->
            _uiState.value = uiState
        }.launchIn(viewModelScope)
    }


    fun onSubtopicToggleCompletion(subtopicId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            // Find the current Topic from the UI state to preserve other properties like notes
            val currentTopic = (_uiState.value as? RoadmapUiState.Success)?.phases
                ?.flatMap { it.topics }
                ?.flatMap { it.subtopics }
                ?.find { it.id == subtopicId }

            val newProgress = TopicProgress(
                subtopicId = subtopicId,
                isCompleted = isCompleted,
                lastAccessedDate = Date(), // Update last accessed date
                notes = currentTopic?.notes // Preserve existing notes
            )
            updateTopicProgressUseCase(newProgress)
        }
    }

}

sealed interface RoadmapUiState {
    data object Loading : RoadmapUiState
    data class Success(val phases: List<Phase>) : RoadmapUiState
    data class Error(val error: String) : RoadmapUiState
}
