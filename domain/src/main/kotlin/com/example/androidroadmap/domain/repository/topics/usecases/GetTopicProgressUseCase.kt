package com.example.androidroadmap.domain.repository.topics.usecases

import com.example.androidroadmap.domain.repository.topics.repository.RoadmapRepository
import com.example.androidroadmap.domain.repository.topics.TopicProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicProgressUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    operator fun invoke(subtopicId: String): Flow<TopicProgress?> {
        return repository.getTopicProgress(subtopicId)
    }
}
