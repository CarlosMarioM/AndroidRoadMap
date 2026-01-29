package com.example.androidroadmap.domain.roadmap_progress

import com.example.androidroadmap.model.Phase

interface RoadmapDataSource {
    suspend fun getPhases(): List<Phase>
    suspend fun getSubtopicContent(subtopicId: String): String?
}
