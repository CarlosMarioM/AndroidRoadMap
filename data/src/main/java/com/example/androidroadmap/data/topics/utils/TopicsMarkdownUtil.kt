package com.example.androidroadmap.data.topics.utils

import android.content.Context
import android.util.Log
import com.example.androidroadmap.model.Subtopic
import com.example.androidroadmap.model.TopicsRoot
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsMarkdownUtil @Inject
    constructor(private val context: Context, private val json : Json) {
    private val listPath : String = "content/topics_list.json"
    fun readMarkdown(path: String): String =
        try {
            context.assets.open(path)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            throw IllegalStateException("Markdown not found: $path", e)
        }

    fun readTopics(): TopicsRoot {
        return try {
            val value = context.assets
                .open(listPath)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString(TopicsRoot.serializer(), value)
        }
        catch (e: Exception) {
            throw IllegalStateException("Topics not found", e)
        }
    }

    fun findSubtopicById(subtopicId: String, topicsRoot: TopicsRoot): Subtopic? {
        val topic = topicsRoot.phases
            .flatMap { it.topics }
            .flatMap { it.subtopics }
            .find { it.id == subtopicId }

        if (topic == null) {
            Log.d("SubtopicLookup", "No subtopic found for id: $subtopicId")
        }

        return topic
    }
}
