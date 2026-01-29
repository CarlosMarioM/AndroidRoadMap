package com.example.androidroadmap.data.roadmap_progress

import com.example.androidroadmap.data.roadmap_progress.local.TopicProgressDao
import com.example.androidroadmap.data.roadmap_progress.mappers.toDomain
import com.example.androidroadmap.data.roadmap_progress.mappers.toEntity
import com.example.androidroadmap.domain.roadmap_progress.Phase
import com.example.androidroadmap.domain.roadmap_progress.RoadmapDataSource
import com.example.androidroadmap.domain.roadmap_progress.Topic
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RoadmapRepositoryImplTest {

    @MockK
    lateinit var mockRoadmapDataSource: RoadmapDataSource

    @MockK
    lateinit var mockTopicProgressDao: TopicProgressDao

    private lateinit var roadmapRepositoryImpl: RoadmapRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true) // Initialize mocks
        roadmapRepositoryImpl = RoadmapRepositoryImpl(mockRoadmapDataSource, mockTopicProgressDao)
    }

    @Test
    fun `getPhases returns data from data source`() = runTest {
        val expectedPhases = listOf(
            Phase(
                id = "phase_x",
                title = "Phase X",
                order = 0,
                topics = listOf(
                    Topic(id = "topic_x1", title = "Topic X1", contentPath = null, phaseId = "phase_x", order = 0)
                )
            )
        )
        coEvery { mockRoadmapDataSource.getPhases() } returns expectedPhases

        val result = roadmapRepositoryImpl.getPhases()

        assertEquals(expectedPhases, result)
        coVerify(exactly = 1) { mockRoadmapDataSource.getPhases() }
    }

    @Test
    fun `getTopicContent returns data from data source`() = runTest {
        val topicId = "topic_y"
        val expectedContent = "Content for topic Y"
        coEvery { mockRoadmapDataSource.getTopicContent(topicId) } returns expectedContent

        val result = roadmapRepositoryImpl.getTopicContent(topicId)

        assertEquals(expectedContent, result)
        coVerify(exactly = 1) { mockRoadmapDataSource.getTopicContent(topicId) }
    }

    @Test
    fun `getTopicProgress returns data from dao mapped to domain`() = runTest {
        val topicId = "topic_z"
        val entity = com.example.androidroadmap.data.roadmap_progress.local.TopicProgressEntity(
            topicId,
            true,
            Date(1),
            "Notes"
        )
        every { mockTopicProgressDao.getTopicProgress(topicId) } returns flowOf(entity)

        val resultFlow = roadmapRepositoryImpl.getTopicProgress(topicId)
        val result = resultFlow.first()

        assertEquals(entity.toDomain(), result)
        coVerify(exactly = 1) { mockTopicProgressDao.getTopicProgress(topicId) }
    }

    @Test
    fun `updateTopicProgress calls dao insert with mapped entity`() = runTest {
        val domainProgress = TopicProgress("topic_a", true, Date(2), "Updated")
        val entity = domainProgress.toEntity() // Assuming toEntity extension function exists

        coEvery { mockTopicProgressDao.insertTopicProgress(entity) } returns Unit

        roadmapRepositoryImpl.updateTopicProgress(domainProgress)

        coVerify(exactly = 1) { mockTopicProgressDao.insertTopicProgress(entity) }
    }

    @Test
    fun `getAllTopicProgress returns all data from dao mapped to domain`() = runTest {
        val entity1 = com.example.androidroadmap.data.roadmap_progress.local.TopicProgressEntity("topic_b1", true, Date(3), null)
        val entity2 = com.example.androidroadmap.data.roadmap_progress.local.TopicProgressEntity("topic_b2", false, Date(4), "Notes2")
        every { mockTopicProgressDao.getAllTopicProgress() } returns flowOf(listOf(entity1, entity2))

        val resultFlow = roadmapRepositoryImpl.getAllTopicProgress()
        val result = resultFlow.first()

        assertEquals(2, result.size)
        assertEquals(entity1.toDomain(), result[0])
        assertEquals(entity2.toDomain(), result[1])
        coVerify(exactly = 1) { mockTopicProgressDao.getAllTopicProgress() }
    }
}
