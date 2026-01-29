package com.example.androidroadmap.data.roadmap_progress

import com.example.androidroadmap.data.roadmap_progress.local.TopicProgressDao
import com.example.androidroadmap.data.roadmap_progress.local.TopicProgressEntity
import com.example.androidroadmap.domain.roadmap_progress.RoadmapDataSource
import com.example.androidroadmap.domain.roadmap_progress.RoadmapRepository
import com.example.androidroadmap.domain.roadmap_progress.TopicProgress
import com.example.androidroadmap.model.Phase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class RoadmapRepositoryImpl @Inject constructor(
    private val roadmapDataSource: RoadmapDataSource,
    private val topicProgressDao: TopicProgressDao
) : RoadmapRepository {
    override suspend fun getPhases(): List<Phase> {
        return roadmapDataSource.getPhases()
    }
    override suspend fun getSubtopicContent(subtopicId: String): String? {
        return roadmapDataSource.getSubtopicContent(subtopicId)
    }

    override fun getTopicProgress(subtopicId: String): Flow<TopicProgress?> {
        return topicProgressDao.getTopicProgress(subtopicId).map { it?.toDomain() }
    }

    override suspend fun updateTopicProgress(topicProgress: TopicProgress) {
        topicProgressDao.insertTopicProgress(topicProgress.toEntity())
    }

    override fun getAllTopicProgress(): Flow<List<TopicProgress>> {
        return topicProgressDao.getAllTopicProgress().map { list -> list.map { it.toDomain() } }
    }

    fun TopicProgressEntity.toDomain(): TopicProgress {
        return TopicProgress(
            subtopicId = subtopicId,
            isCompleted = isCompleted,
            lastAccessedDate = lastAccessedDate,
            notes = notes,
        )
    }

    fun TopicProgress.toEntity(): TopicProgressEntity {
        return TopicProgressEntity(
            subtopicId = subtopicId,
            isCompleted = isCompleted,
            lastAccessedDate = lastAccessedDate,
            notes = notes,
        )
    }
}
