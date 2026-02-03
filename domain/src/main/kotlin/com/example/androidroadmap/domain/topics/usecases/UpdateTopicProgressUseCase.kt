package com.example.androidroadmap.domain.topics.usecases

import com.example.androidroadmap.domain.topics.repository.RoadmapRepository
import com.example.androidroadmap.domain.topics.TopicProgress
import javax.inject.Inject

class UpdateTopicProgressUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(topicProgress: TopicProgress) {
        repository.updateTopicProgress(topicProgress)
    }
}
