package com.example.androidroadmap.domain.topics.repository

import com.example.androidroadmap.model.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<Topic>>
    suspend fun updateTopicProgress(topicId: String, progress: Float)
    suspend fun getTopicById(topicId: String): Topic?
    suspend fun insertAll(topics: List<Topic>)
}