package com.example.androidroadmap.data.topics.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicProgressDao {
    @Query("SELECT * FROM topic_progress WHERE subtopicId = :subtopicId")
    fun getTopicProgress(subtopicId: String): Flow<TopicProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicProgress(progress: TopicProgressEntity)

    @Query("SELECT * FROM topic_progress")
    fun getAllTopicProgress(): Flow<List<TopicProgressEntity>>
}
