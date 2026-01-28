package com.example.androidroadmap.features.topics

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.markdown.MarkdownActivity
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil
import com.example.androidroadmap.features.topics.ui.SubtopicsScreen
import com.example.androidroadmap.features.topics.ui.TopicsScreen
import com.example.androidroadmap.model.Topic
import com.example.androidroadmap.model.Subtopic

class TopicsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val phaseIdString = intent.getStringExtra("PHASE_ID") ?: return

        val util = TopicsMarkdownUtil()
        val topicsRoot = util.readTopics(this)

        // Find the topic
        val topics: List<Topic> = topicsRoot.let {
            root -> root.phases.filter { phase -> phase.id == phaseIdString }
            .flatMap { phase -> phase.topics }
        }


        setContentView(
            ComposeView(this).apply{
                setContent {
                    TopicsScreen(
                        topics = topics,
                        onClick = { topic ->
                            println(topic)
                            startActivity(
                                Intent(
                                    this@TopicsActivity,
                                    SubtopicsActivity::class.java)
                                    .apply {
                                        putExtra("TOPIC_ID", topic.id)
                                    }
                            )
                        }
                    )
                }
            }
        )
    }
}
