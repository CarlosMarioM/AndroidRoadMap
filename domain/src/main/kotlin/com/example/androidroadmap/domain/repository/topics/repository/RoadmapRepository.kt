package com.example.androidroadmap.domain.repository.topics.repository

import com.example.androidroadmap.domain.repository.topics.TopicProgress
import com.example.androidroadmap.domain.model.topic.Phase
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    suspend fun getPhases(): List<Phase>
    suspend fun getSubtopicContent(subtopicId: String): String?
    fun getTopicProgress(subtopicId: String): Flow<TopicProgress?>
    suspend fun updateTopicProgress(topicProgress: TopicProgress)
    fun getAllTopicProgress(): Flow<List<TopicProgress>>
}