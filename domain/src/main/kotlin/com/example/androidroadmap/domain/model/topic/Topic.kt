package com.example.androidroadmap.domain.model.topic

import kotlinx.serialization.*
import java.util.Date

@Serializable
data class TopicsRoot(
    val domain: String,
    val phases: List<Phase>
)

@Serializable
data class Phase(
    val id: String,
    val title: String,
    val order: Int,
    val topics: List<Topic>
)

@Serializable
data class Topic(
    val id: String,
    val title: String,
    val subtopics: List<Subtopic>
)

@Serializable
data class Subtopic(
    val id: String,
    val title: String,
    val path: String,                // folder README.md path
    val examples: List<Example>,
    val isCompleted: Boolean = false,
    val lastAccessedDate: @Contextual Date? = null,
    val notes: String? = null,
    )

@Serializable
data class Example(
    val id: String,
    val title: String,
    val description: String,
    val contentKey: String           // Composable key or identifier
)
