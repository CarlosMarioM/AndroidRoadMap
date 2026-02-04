package com.example.androidroadmap.domain.repository.topics.usecases

import com.example.androidroadmap.domain.repository.topics.repository.RoadmapRepository
import com.example.androidroadmap.domain.model.topic.Phase
import javax.inject.Inject

class GetRoadmapPhasesUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(): List<Phase> {
        return repository.getPhases()
    }
}
