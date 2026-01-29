package com.example.androidroadmap.data.roadmap_progress.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TopicProgressEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class) // For Date type
abstract class RoadmapDatabase : RoomDatabase() {
    abstract fun topicProgressDao(): TopicProgressDao
}
