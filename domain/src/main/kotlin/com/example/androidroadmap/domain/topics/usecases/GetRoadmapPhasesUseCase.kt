package com.example.androidroadmap.domain.topics.usecases

import com.example.androidroadmap.domain.topics.repository.RoadmapRepository
import com.example.androidroadmap.model.Phase
import javax.inject.Inject

class GetRoadmapPhasesUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(): List<Phase> {
        return repository.getPhases()
    }
}
