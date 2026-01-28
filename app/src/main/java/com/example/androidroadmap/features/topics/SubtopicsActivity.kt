package com.example.androidroadmap.features.topics

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.markdown.MarkdownActivity
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil
import com.example.androidroadmap.features.topics.ui.SubtopicsScreen
import com.example.androidroadmap.model.Subtopic
import com.example.androidroadmap.model.Topic

class SubtopicsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topicIdString = intent.getStringExtra("TOPIC_ID") ?: return
        val util = TopicsMarkdownUtil()
        val topicsRoot = util.readTopics(this)

        val topicWithSubtopics: Topic? = topicsRoot.phases
            .flatMap { it.topics }
            .find { topic -> topic.id == topicIdString } // all topics in all phases


        val subtopics: List<Subtopic> = topicWithSubtopics?.subtopics ?: emptyList()
        val topicTitle: String = topicWithSubtopics?.title ?: "Unknown Topic"

        setContentView(
            ComposeView(this).apply {
                setContent {
                    SubtopicsScreen(
                        title = topicTitle,
                        subtopics = subtopics,
                        onSubtopicClick = { subtopic ->
                            startActivity(
                                Intent(
                                    this@SubtopicsActivity,
                                    MarkdownActivity::class.java
                                ).apply {
                                    putExtra("SUBTOPIC_ID", subtopic.id)
                                }
                            )
                        }
                    )
                }
            }
        )
    }
}