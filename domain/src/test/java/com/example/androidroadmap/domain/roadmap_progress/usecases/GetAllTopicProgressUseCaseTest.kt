package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.FakeRoadmapRepository
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAllTopicProgressUseCaseTest {

    private lateinit var getAllTopicProgressUseCase: GetAllTopicProgressUseCase
    private lateinit var fakeRepository: FakeRoadmapRepository

    @Before
    fun setup() {
        fakeRepository = FakeRoadmapRepository()
        getAllTopicProgressUseCase = GetAllTopicProgressUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns all topic progress from repository`() = runTest {
        val progress1 = TopicProgress("topic1", true, Date(1), "Note 1")
        val progress2 = TopicProgress("topic2", false, Date(2), null)
        fakeRepository.setTopicProgress(mapOf(
            "topic1" to progress1,
            "topic2" to progress2
        ))

        val result = getAllTopicProgressUseCase().first()
        assertEquals(2, result.size)
        assertTrue(result.contains(progress1))
        assertTrue(result.contains(progress2))
    }

    @Test
    fun `invoke returns empty list when no topic progress`() = runTest {
        fakeRepository.setTopicProgress(emptyMap())

        val result = getAllTopicProgressUseCase().first()
        assertTrue(result.isEmpty())
    }
}
