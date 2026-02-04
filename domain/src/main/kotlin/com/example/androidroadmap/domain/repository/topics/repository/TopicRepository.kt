package com.example.androidroadmap.domain.repository.topics.repository

import com.example.androidroadmap.domain.model.topic.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<Topic>>
    suspend fun updateTopicProgress(topicId: String, progress: Float)
    suspend fun getTopicById(topicId: String): Topic?
    suspend fun insertAll(topics: List<Topic>)
}