package com.example.androidroadmap.domain.topics.usecases

import com.example.androidroadmap.domain.topics.repository.RoadmapRepository
import javax.inject.Inject

class GetTopicContentUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(subtopicId: String): String? {
        return repository.getSubtopicContent(subtopicId)
    }
}
