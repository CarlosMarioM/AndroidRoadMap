package com.example.androidroadmap.features.markdown  

import MarkdownScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidroadmap.features.markdown.utils.TopicsMarkdownUtil

class MarkdownActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subtopicId = intent.getStringExtra("SUBTOPIC_ID") ?: return

        val util = TopicsMarkdownUtil()
        val topicsRoot = util.readTopics(this)

        // Traverse phases -> topics -> subtopics
        val subtopic = topicsRoot.let { it ->
            it.phases
                .flatMap { it.topics }
                .flatMap { it.subtopics }
                .find { it.id == subtopicId }
        }
        
        try {
            val markdownPath = subtopic?.path ?: ""
            val markdown = if (markdownPath.isNotEmpty()) {
                util.readMarkdown(this, markdownPath)
            } else {
                "Markdown not found for subtopic: $subtopicId"
            }

            setContent {
                MarkdownScreen(markdown = markdown)
            }
        }catch (e: Exception){
            println("Error: $e")
        }
    }
}
