package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.FakeRoadmapRepository
import com.example.androidroadmap.domain.topics.TopicProgress
import com.example.androidroadmap.domain.topics.usecases.UpdateTopicProgressUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateTopicProgressUseCaseTest {

    private lateinit var updateTopicProgressUseCase: UpdateTopicProgressUseCase
    private lateinit var fakeRepository: FakeRoadmapRepository

    @Before
    fun setup() {
        fakeRepository = FakeRoadmapRepository()
        updateTopicProgressUseCase = UpdateTopicProgressUseCase(fakeRepository)
    }

    @Test
    fun `invoke updates topic progress in repository`() = runTest {
        val topicId = "topic_to_update"
        val initialProgress = TopicProgress(topicId, false, Date(0), null)
        fakeRepository.setTopicProgress(mapOf(topicId to initialProgress))

        val updatedProgress = TopicProgress(topicId, true, Date(), "Updated notes")
        updateTopicProgressUseCase(updatedProgress)

        val result = fakeRepository.getTopicProgress(topicId).first()
        assertEquals(updatedProgress, result)
        assertTrue(result?.isCompleted == true)
    }

    @Test
    fun `invoke adds new topic progress if not exists`() = runTest {
        val topicId = "new_topic"
        val newProgress = TopicProgress(topicId, true, Date(), "First time completed")
        
        updateTopicProgressUseCase(newProgress)

        val result = fakeRepository.getTopicProgress(topicId).first()
        assertEquals(newProgress, result)
    }
}
