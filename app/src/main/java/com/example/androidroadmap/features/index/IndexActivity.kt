package com.example.androidroadmap.features.index

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import com.example.androidroadmap.features.index.ui.IndexScreen
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil
import com.example.androidroadmap.features.topics.TopicsActivity

class IndexActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        val util = TopicsMarkdownUtil()
        val topics = util.readTopics(this)
        super.onCreate(savedInstanceState)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    IndexScreen(topics = topics, onTopicClick = { topic ->
                        startActivity(
                            Intent(this@IndexActivity, TopicsActivity::class.java).apply {
                                putExtra("TOPIC_ID", topic.id.toString())
                            })
                    })
                }
            }
        )
    }
}