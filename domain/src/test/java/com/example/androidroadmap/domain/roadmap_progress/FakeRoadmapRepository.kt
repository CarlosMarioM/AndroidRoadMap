package com.example.androidroadmap.domain.roadmap_progress

import com.example.androidroadmap.domain.topics.TopicProgress
import com.example.androidroadmap.domain.topics.repository.RoadmapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRoadmapRepository : RoadmapRepository {

    private val _phases = MutableStateFlow<List<Phase>>(emptyList())
    private val _topicProgress = MutableStateFlow<MutableMap<String, TopicProgress>>(mutableMapOf())
    private val _topicContents = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())

    fun setPhases(phases: List<Phase>) {
        _phases.value = phases
    }

    fun setTopicProgress(progressMap: Map<String, TopicProgress>) {
        _topicProgress.value = progressMap.toMutableMap()
    }

    fun setTopicContent(topicId: String, content: String?) {
        if (content != null) {
            _topicContents.value[topicId] = content
        } else {
            _topicContents.value.remove(topicId)
        }
    }

    override suspend fun getPhases(): List<Phase> {
        return _phases.value
    }

    override suspend fun getTopicContent(topicId: String): String? {
        return _topicContents.value[topicId]
    }

    override fun getTopicProgress(topicId: String): Flow<TopicProgress?> {
        return _topicProgress.map { it[topicId] }
    }

    override suspend fun updateTopicProgress(topicProgress: TopicProgress) {
        val current = _topicProgress.value
        current[topicProgress.topicId] = topicProgress
        _topicProgress.value = current.toMutableMap() // Trigger flow emission
    }

    override fun getAllTopicProgress(): Flow<List<TopicProgress>> {
        return _topicProgress.map { it.values.toList() }
    }
}