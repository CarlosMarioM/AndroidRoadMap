package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.FakeRoadmapRepository
import com.example.androidroadmap.domain.roadmap_progress.Phase
import com.example.androidroadmap.domain.roadmap_progress.Topic
import com.example.androidroadmap.domain.topics.usecases.GetRoadmapPhasesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetRoadmapPhasesUseCaseTest {

    private lateinit var getRoadmapPhasesUseCase: GetRoadmapPhasesUseCase
    private lateinit var fakeRepository: FakeRoadmapRepository

    @Before
    fun setup() {
        fakeRepository = FakeRoadmapRepository()
        getRoadmapPhasesUseCase = GetRoadmapPhasesUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns phases from repository`() = runTest {
        val phases = listOf(
            Phase(
                id = "phase_a",
                title = "Phase A",
                order = 0,
                topics = listOf(
                    Topic(id = "topic_a1", title = "Topic A1", contentPath = null, phaseId = "phase_a", order = 0),
                    Topic(id = "topic_a2", title = "Topic A2", contentPath = null, phaseId = "phase_a", order = 1)
                )
            )
        )
        fakeRepository.setPhases(phases)

        val result = getRoadmapPhasesUseCase()

        assertEquals(phases, result)
    }

    @Test
    fun `invoke returns empty list when repository has no phases`() = runTest {
        fakeRepository.setPhases(emptyList())

        val result = getRoadmapPhasesUseCase()

        assertEquals(emptyList(), result)
    }
}
