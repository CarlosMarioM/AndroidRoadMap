package com.example.androidroadmap.domain.topics

import com.example.androidroadmap.model.Phase

interface RoadmapDataSource {
    suspend fun getPhases(): List<Phase>
    suspend fun getSubtopicContent(subtopicId: String): String?
}
