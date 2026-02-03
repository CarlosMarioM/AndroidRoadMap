package com.example.androidroadmap.data.topics

import android.content.Context
import androidx.room.Room
import com.example.androidroadmap.data.topics.local.RoadmapDatabase
import com.example.androidroadmap.data.topics.local.TopicProgressDao
import com.example.androidroadmap.data.topics.utils.TopicsMarkdownUtil
import com.example.androidroadmap.domain.topics.RoadmapDataSource
import com.example.androidroadmap.domain.topics.repository.RoadmapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoadmapDataModule {

    @Singleton
    @Provides
    fun provideTopicsMarkdownUtil(@ApplicationContext context : Context, json : Json): TopicsMarkdownUtil {
        return TopicsMarkdownUtil(context, json)
    }

    @Singleton
    @Provides
    fun provideRoadmapDataSource(assetRoadmapDataSource: AssetRoadmapDataSource): RoadmapDataSource {
        return assetRoadmapDataSource
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
    }

    // Room Database Providers
    @Singleton
    @Provides
    fun provideRoadmapDatabase(
        @ApplicationContext context: Context
    ): RoadmapDatabase {
        return Room.databaseBuilder(
            context,
            RoadmapDatabase::class.java,
            "roadmap_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideTopicProgressDao(database: RoadmapDatabase): TopicProgressDao {
        return database.topicProgressDao()
    }

    // Repository Provider
    @Singleton
    @Provides
    fun provideRoadmapRepository(
        roadmapRepositoryImpl: RoadmapRepositoryImpl
    ): RoadmapRepository {
        return roadmapRepositoryImpl
    }
}