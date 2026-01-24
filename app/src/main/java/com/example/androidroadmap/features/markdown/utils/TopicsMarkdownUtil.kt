package com.example.androidroadmap.features.markdown.utils

import com.example.androidroadmap.model.Topic
import com.example.androidroadmap.model.TopicId
import android.content.Context
import android.util.Log
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class TopicsMarkdownUtil {
    companion object {
        private var topics : List<Topic> = listOf()
    }

    fun readMarkdown(context: Context, path: String): String {
        return try {
            context.assets.open(path)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            throw IllegalStateException("Markdown not found: $path", e)
        }
    }

    fun readTopics(context: Context): List<Topic> {
      try {
          val json = context.assets
              .open("content/phases.json")
              .bufferedReader()
              .use { it.readText() }

          val list : List<Topic> = Json.decodeFromString(ListSerializer(Topic.serializer()), json)
          topics += list
          return topics
      }catch (e : Exception){
          println(e)
          return listOf<Topic>()
      }
    }

    fun readMarkdownFromTopicId(id: TopicId): Topic? {
        if (topics.isEmpty()) throw NotImplementedError("Topics is Empty")

        val topic = topics.find {
            it.id.toString() == id.id// ensure string comparison
        }

        if (topic == null) {
            Log.d("TopicLookup", "No topic found for id: $id")
            topics.forEach { Log.d("TopicLookup", "Existing topic id: ${it.id}") }
        }

        return topic
    }
}
