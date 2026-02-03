package com.example.androidroadmap.features.markdown

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.topics.TopicProgress
import com.example.androidroadmap.domain.topics.usecases.GetTopicContentUseCase
import com.example.androidroadmap.domain.topics.usecases.GetTopicProgressUseCase
import com.example.androidroadmap.domain.topics.usecases.UpdateTopicProgressUseCase
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
class MarkdownViewModel @Inject constructor(
    private val getTopicContentUseCase: GetTopicContentUseCase,
    private val getTopicProgressUseCase: GetTopicProgressUseCase,
    private val updateTopicProgressUseCase: UpdateTopicProgressUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val subtopicId: String = savedStateHandle.get<String>("SUBTOPIC_ID")
        ?: error("SUBTOPIC_ID is required for MarkdownViewModel")
    private val _uiState = MutableStateFlow<MarkdownUiState>(MarkdownUiState.Loading)
    val uiState : StateFlow<MarkdownUiState> = _uiState
    private val _content = MutableStateFlow<String?>(null)

    init {
        loadContentAndProgress()
    }

    private fun loadContentAndProgress() {
        viewModelScope.launch {
            _uiState.value = MarkdownUiState.Loading
            try {
                // Fetch content
                val content = getTopicContentUseCase(subtopicId)
                _content.value = content

                combine(_content, getTopicProgressUseCase(subtopicId)){ markdown, progress ->
                   if(markdown != null){
                       MarkdownUiState.Success(markdown,
                           isCompleted = progress?.isCompleted ?: false
                       )
                   } else {
                       MarkdownUiState.Error("Failed to load markdown $subtopicId")
                   }
                }.onEach { combinedState ->
                    _uiState.value = combinedState
                }.launchIn(viewModelScope)

            } catch (e: Exception) {
                _uiState.value = MarkdownUiState.Error("Failed to load markdown content: ${e.message}")
            }
        }
    }

    fun onScrolledToEnd() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MarkdownUiState.Success && !currentState.isCompleted) {
                val newProgress = TopicProgress(
                    subtopicId = subtopicId,
                    isCompleted = true,
                    lastAccessedDate = Date(),
                    notes = null
                )
                updateTopicProgressUseCase(newProgress)
            }
        }
    }
}

sealed interface MarkdownUiState {
    data class Success(val content: String, val isCompleted: Boolean) : MarkdownUiState
    data class Error(val message: String) : MarkdownUiState
    data object Loading : MarkdownUiState
}