package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.FakeRoadmapRepository
import com.example.androidroadmap.domain.repository.topics.TopicProgress
import com.example.androidroadmap.domain.repository.topics.usecases.GetTopicProgressUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetTopicProgressUseCaseTest {

    private lateinit var getTopicProgressUseCase: GetTopicProgressUseCase
    private lateinit var fakeRepository: FakeRoadmapRepository

    @Before
    fun setup() {
        fakeRepository = FakeRoadmapRepository()
        getTopicProgressUseCase = GetTopicProgressUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns topic progress from repository`() = runTest {
        val topicId = "topic_x"
        val expectedProgress = TopicProgress(topicId, true, Date(), "Notes")
        fakeRepository.setTopicProgress(mapOf(topicId to expectedProgress))

        val result = getTopicProgressUseCase(topicId).first()
        assertEquals(expectedProgress, result)
    }

    @Test
    fun `invoke returns null when no progress for topic`() = runTest {
        val topicId = "topic_y"
        // No progress set for topic_y in fakeRepository

        val result = getTopicProgressUseCase(topicId).first()
        assertNull(result)
    }
}
