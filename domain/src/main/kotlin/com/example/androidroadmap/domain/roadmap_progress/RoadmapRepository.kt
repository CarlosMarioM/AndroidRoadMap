package com.example.androidroadmap.domain.roadmap_progress

import com.example.androidroadmap.model.Phase
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    suspend fun getPhases(): List<Phase>
    suspend fun getSubtopicContent(subtopicId: String): String?
    fun getTopicProgress(subtopicId: String): Flow<TopicProgress?>
    suspend fun updateTopicProgress(topicProgress: TopicProgress)
    fun getAllTopicProgress(): Flow<List<TopicProgress>>
}
