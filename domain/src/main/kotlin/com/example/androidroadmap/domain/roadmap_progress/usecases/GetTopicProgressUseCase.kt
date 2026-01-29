package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.RoadmapRepository
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicProgressUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    operator fun invoke(subtopicId: String): Flow<TopicProgress?> {
        return repository.getTopicProgress(subtopicId)
    }
}
