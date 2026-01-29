package com.example.androidroadmap.domain.roadmap_progress.usecases

import com.example.androidroadmap.domain.roadmap_progress.RoadmapRepository
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import javax.inject.Inject

class UpdateTopicProgressUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(topicProgress: TopicProgress) {
        repository.updateTopicProgress(topicProgress)
    }
}
