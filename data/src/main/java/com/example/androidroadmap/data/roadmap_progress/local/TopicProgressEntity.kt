package com.example.androidroadmap.data.roadmap_progress.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "topic_progress")
data class TopicProgressEntity(
    @PrimaryKey val subtopicId: String,
    val isCompleted: Boolean,
    val lastAccessedDate: Date,
    val notes: String?
)
