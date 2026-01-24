package com.example.androidroadmap.features.topics

import MarkdownScreen
import com.example.androidroadmap.model.TopicId
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil

class TopicsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topicId : TopicId = TopicId.fromString((intent.getStringExtra("TOPIC_ID") ?: return))
        val util = TopicsMarkdownUtil()
        val path = util.readMarkdownFromTopicId(topicId)?.path ?: ""
        val markdown = util.readMarkdown(this, path)

        setContent {
            MarkdownScreen(markdown = markdown)
        }
    }
}