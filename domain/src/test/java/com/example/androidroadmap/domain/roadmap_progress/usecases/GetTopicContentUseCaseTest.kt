package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.FakeRoadmapRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetTopicContentUseCaseTest {

    private lateinit var getTopicContentUseCase: GetTopicContentUseCase
    private lateinit var fakeRepository: FakeRoadmapRepository

    @Before
    fun setup() {
        fakeRepository = FakeRoadmapRepository()
        getTopicContentUseCase = GetTopicContentUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns topic content from repository`() = runTest {
        val topicId = "some_topic_id"
        val expectedContent = "This is the content for some_topic_id."
        fakeRepository.setTopicContent(topicId, expectedContent)

        val result = getTopicContentUseCase(topicId)
        assertEquals(expectedContent, result)
    }

    @Test
    fun `invoke returns null if repository has no content for topic`() = runTest {
        val topicId = "non_existent_topic"
        // No content set for this topicId in the fake repository

        val result = getTopicContentUseCase(topicId)
        assertNull(result)
    }
}
