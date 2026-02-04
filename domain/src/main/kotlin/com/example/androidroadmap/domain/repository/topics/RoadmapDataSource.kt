package com.example.androidroadmap.domain.repository.topics

import com.example.androidroadmap.domain.model.topic.Phase

interface RoadmapDataSource {
    suspend fun getPhases(): List<Phase>
    suspend fun getSubtopicContent(subtopicId: String): String?
}
