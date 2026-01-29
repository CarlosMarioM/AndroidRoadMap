package com.example.androidroadmap.domain.roadmap_progress

import java.util.Date

data class TopicProgress(
    val subtopicId: String,
    val isCompleted: Boolean,
    val lastAccessedDate: Date,
    val notes: String?
)
