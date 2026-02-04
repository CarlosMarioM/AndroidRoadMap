package com.example.androidroadmap.data.topics

import com.example.androidroadmap.data.topics.utils.TopicsMarkdownUtil
import com.example.androidroadmap.domain.repository.topics.RoadmapDataSource
import com.example.androidroadmap.domain.model.topic.Phase
import com.example.androidroadmap.domain.model.topic.Subtopic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRoadmapDataSource @Inject constructor(
    private val markdownUtil: TopicsMarkdownUtil,
) : RoadmapDataSource {
    private val contentNotFount : String = "CONTENT_NOT_FOUND"
    private var cachedPhases: List<Phase>? = null
    private val cachedSubtopicContents: MutableMap<String, String> = mutableMapOf()
    private var allSubtopics: Map<String, Subtopic> = emptyMap()

    override suspend fun getPhases(): List<Phase> {
        if (cachedPhases == null) {
            val topicsRoot = markdownUtil.readTopics()
            cachedPhases = topicsRoot.phases
            allSubtopics = topicsRoot.phases
                .flatMap { phase -> phase.topics}
                .flatMap { topic -> topic.subtopics}
                .associateBy { it.id }
        }
        return cachedPhases!!
    }

    override suspend fun getSubtopicContent(subtopicId: String): String? {
        getPhases()

        val subtopic = allSubtopics[subtopicId]
        val contentPath = subtopic?.path

        return if(contentPath != null) {
                cachedSubtopicContents.getOrPut(subtopicId) {
                    try {
                        markdownUtil.readMarkdown(contentPath)
                    } catch (e: Exception) {
                        System.err.println("Error reading topic content from asset: $contentPath, Error: ${e.message}")
                        contentNotFount
                    }
                }
        } else {
            System.err.println("Subtopic with ID $subtopicId not found or has no content path.")
             null
            }
        }
}