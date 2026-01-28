package com.example.androidroadmap.features.markdown.utils

import android.content.Context
import android.util.Log
import com.example.androidroadmap.model.Topic
import com.example.androidroadmap.model.TopicsRoot
import kotlinx.serialization.json.Json

class TopicsMarkdownUtil {

    companion object {
        private var root: TopicsRoot? = null
    }

    fun readMarkdown(context: Context, path: String): String =
        try {
            context.assets.open(path)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            throw IllegalStateException("Markdown not found: $path", e)
        }

    fun readTopics(context: Context): TopicsRoot {
        if (root != null) return root!!

        val json = context.assets
            .open("content/topics_list.json")
            .bufferedReader()
            .use { it.readText() }

        root = Json {
            ignoreUnknownKeys = true
        }.decodeFromString(TopicsRoot.serializer(), json)

        return root!!
    }

    fun findTopicById(id: String): Topic? {
        val data = root ?: error("Topics not loaded. Call readTopics() first.")

        val topic = data.phases
            .flatMap { it.topics }
            .find { it.id == id }

        if (topic == null) {
            Log.d("TopicLookup", "No topic found for id: $id")
        }

        return topic
    }
}
