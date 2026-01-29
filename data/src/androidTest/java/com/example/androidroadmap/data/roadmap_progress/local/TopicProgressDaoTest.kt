package com.example.androidroadmap.data.roadmap_progress.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class TopicProgressDaoTest {

    private lateinit var topicProgressDao: TopicProgressDao
    private lateinit var db: RoadmapDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, RoadmapDatabase::class.java
        ).allowMainThreadQueries().build()
        topicProgressDao = db.topicProgressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetTopicProgress() = runTest {
        val topicId = "kotlin_syntax"
        val date = Date()
        val progress = TopicProgressEntity(topicId, true, date, "Mastered basics")
        topicProgressDao.insertTopicProgress(progress)

        val retrievedProgress = topicProgressDao.getTopicProgress(topicId).first()
        assertNotNull(retrievedProgress)
        assertEquals(topicId, retrievedProgress.topicId)
        assertTrue(retrievedProgress.isCompleted)
        assertEquals(date.time, retrievedProgress.lastAccessedDate.time)
        assertEquals("Mastered basics", retrievedProgress.notes)
    }

    @Test
    fun updateTopicProgress() = runTest {
        val topicId = "kotlin_syntax"
        val initialDate = Date(1000)
        val initialProgress = TopicProgressEntity(topicId, false, initialDate, null)
        topicProgressDao.insertTopicProgress(initialProgress)

        val updatedDate = Date(2000)
        val updatedProgress = TopicProgressEntity(topicId, true, updatedDate, "Reviewed")
        topicProgressDao.insertTopicProgress(updatedProgress)

        val retrievedProgress = topicProgressDao.getTopicProgress(topicId).first()
        assertNotNull(retrievedProgress)
        assertEquals(topicId, retrievedProgress.topicId)
        assertTrue(retrievedProgress.isCompleted)
        assertEquals(updatedDate.time, retrievedProgress.lastAccessedDate.time)
        assertEquals("Reviewed", retrievedProgress.notes)
    }

    @Test
    fun getNonExistentTopicProgressReturnsNull() = runTest {
        val topicId = "non_existent"
        val retrievedProgress = topicProgressDao.getTopicProgress(topicId).first()
        assertNull(retrievedProgress)
    }

    @Test
    fun getAllTopicProgress() = runTest {
        val progress1 = TopicProgressEntity("topic1", true, Date(1), "Notes1")
        val progress2 = TopicProgressEntity("topic2", false, Date(2), "Notes2")
        topicProgressDao.insertTopicProgress(progress1)
        topicProgressDao.insertTopicProgress(progress2)

        val allProgress = topicProgressDao.getAllTopicProgress().first()
        assertEquals(2, allProgress.size)
        assertTrue(allProgress.any { it.topicId == "topic1" && it.isCompleted })
        assertTrue(allProgress.any { it.topicId == "topic2" && !it.isCompleted })
    }

    @Test
    fun getAllTopicProgressReturnsEmptyWhenNoData() = runTest {
        val allProgress = topicProgressDao.getAllTopicProgress().first()
        assertTrue(allProgress.isEmpty())
    }
}
