package com.example.androidroadmap.domain.topics

import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<Topic>>
    suspend fun updateTopicProgress(topicId: String, progress: Float)
    suspend fun getTopicById(topicId: String): Topic?
    suspend fun insertAll(topics: List<Topic>)
}
