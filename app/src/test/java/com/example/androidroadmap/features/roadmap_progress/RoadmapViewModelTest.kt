package com.example.androidroadmap.features.roadmap_progress

import com.example.androidroadmap.domain.roadmap_progress.Phase
import com.example.androidroadmap.domain.roadmap_progress.Topic
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import com.example.androidroadmap.domain.roadmap_progress.usecases.GetAllTopicProgressUseCase
import com.example.androidroadmap.domain.roadmap_progress.usecases.GetRoadmapPhasesUseCase
import com.example.androidroadmap.domain.roadmap_progress.usecases.UpdateTopicProgressUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RoadmapViewModelTest {

    @MockK
    lateinit var mockGetRoadmapPhasesUseCase: GetRoadmapPhasesUseCase

    @MockK
    lateinit var mockUpdateTopicProgressUseCase: UpdateTopicProgressUseCase

    @MockK
    lateinit var mockGetAllTopicProgressUseCase: GetAllTopicProgressUseCase

    private lateinit var viewModel: RoadmapViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val samplePhases = listOf(
        Phase(
            id = "phase_a",
            title = "Phase A",
            order = 0,
            topics = listOf(
                Topic(
                    id = "topic_a1",
                    title = "Topic A1",
                    contentPath = null,
                    phaseId = "phase_a",
                    order = 0
                ),
                Topic(
                    id = "topic_a2",
                    title = "Topic A2",
                    contentPath = null,
                    phaseId = "phase_a",
                    order = 1,
                    subtopics = listOf(
                        Topic(
                            id = "subtopic_a2_1",
                            title = "Subtopic A2.1",
                            contentPath = null,
                            phaseId = "phase_a",
                            order = 0
                        )
                    )
                )
            )
        )
    )

    private val sampleTopicProgress = MutableStateFlow<List<TopicProgress>>(emptyList())

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        coEvery { mockGetRoadmapPhasesUseCase() } returns samplePhases
        every { mockGetAllTopicProgressUseCase() } returns sampleTopicProgress

        viewModel = RoadmapViewModel(
            mockGetRoadmapPhasesUseCase,
            mockUpdateTopicProgressUseCase,
            mockGetAllTopicProgressUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        val uiState = viewModel.uiState.value
        assertIs<RoadmapUiState.Loading>(uiState)
    }

    @Test
    fun `roadmap loads successfully and combines with empty progress`() = runTest {
        advanceUntilIdle() // Allow coroutines to complete

        val uiState = viewModel.uiState.value
        assertIs<RoadmapUiState.Success>(uiState)

        assertEquals(samplePhases.size, uiState.phases.size)
        // Check that initial completion status is false
        val topicA1 = uiState.phases[0].topics[0]
        assertEquals("topic_a1", topicA1.id)
        assertFalse(topicA1.isCompleted)
    }

    @Test
    fun `roadmap loads successfully and combines with existing progress`() = runTest {
        val completedTopicId = "topic_a1"
        val progress = TopicProgress(completedTopicId, true, Date(), "Some notes")
        sampleTopicProgress.value = listOf(progress)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertIs<RoadmapUiState.Success>(uiState)

        val topicA1 = uiState.phases[0].topics.find { it.id == completedTopicId }
        assertNotNull(topicA1)
        assertTrue(topicA1.isCompleted)
        assertEquals(progress.lastAccessedDate.time, topicA1.lastAccessedDate?.time)
        assertEquals(progress.notes, topicA1.notes)

        // Check a subtopic
        val subtopicA21 = uiState.phases[0].topics[1].subtopics[0]
        assertFalse(subtopicA21.isCompleted) // Should still be false as no progress for it
    }

    @Test
    fun `onTopicToggleCompletion updates topic progress`() = runTest {
        advanceUntilIdle() // Ensure initial load is complete

        val topicIdToToggle = "topic_a1"
        val isCompleted = true

        viewModel.onTopicToggleCompletion(topicIdToToggle, isCompleted)
        advanceUntilIdle()

        // Verify updateUseCase was called with correct data
        coVerify(exactly = 1) {
            mockUpdateTopicProgressUseCase(
                match {
                    it.topicId == topicIdToToggle && it.isCompleted == isCompleted
                }
            )
        }
    }

    @Test
    fun `onTopicToggleCompletion preserves existing notes`() = runTest {
        val topicId = "topic_a1"
        val initialNotes = "Initial notes"
        val initialProgress = TopicProgress(topicId, false, Date(0), initialNotes)
        sampleTopicProgress.value = listOf(initialProgress)
        advanceUntilIdle()

        val isCompleted = true
        viewModel.onTopicToggleCompletion(topicId, isCompleted)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            mockUpdateTopicProgressUseCase(
                match {
                    it.topicId == topicId && it.isCompleted == isCompleted && it.notes == initialNotes
                }
            )
        }
    }

    @Test
    fun `error state is handled when phases fail to load`() = runTest {
        val errorMessage = "Network error"
        coEvery { mockGetRoadmapPhasesUseCase() } throws Exception(errorMessage)

        // Re-initialize ViewModel after setting mock behavior
        viewModel = RoadmapViewModel(
            mockGetRoadmapPhasesUseCase,
            mockUpdateTopicProgressUseCase,
            mockGetAllTopicProgressUseCase
        )
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertIs<RoadmapUiState.Error>(uiState)
        assertEquals("Failed to load roadmap: $errorMessage", uiState.message)
    }
}
