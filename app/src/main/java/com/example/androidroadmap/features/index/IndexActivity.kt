package com.example.androidroadmap.features.index

import IndexScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil
import com.example.androidroadmap.features.topics.TopicsActivity

class IndexActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val util = TopicsMarkdownUtil()
        val root = util.readTopics(this)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    IndexScreen(
                        phases = root.phases,
                        onPhaseClick = { phase ->
                            startActivity(
                                Intent(
                                    this@IndexActivity,
                                    TopicsActivity::class.java
                                ).apply {
                                    putExtra("PHASE_ID", phase.id)
                                }
                            )
                        }
                    )
                }
            }
        )
    }
}
