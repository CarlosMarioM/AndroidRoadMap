package com.example.androidroadmap.data.roadmap_progress

import android.content.res.AssetManager
import com.example.androidroadmap.data.roadmap_progress.mappers.toDomain
import com.example.androidroadmap.data.roadmap_progress.remote.JsonPhase
import com.example.androidroadmap.data.roadmap_progress.remote.JsonTopic
import com.example.androidroadmap.data.roadmap_progress.remote.RoadmapJson
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AssetRoadmapDataSourceTest {

    @MockK
    lateinit var mockAssetManager: AssetManager

    private lateinit var json: Json
    private lateinit var dataSource: AssetRoadmapDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        json = Json { ignoreUnknownKeys = true }
        dataSource = AssetRoadmapDataSource(mockAssetManager, json)
    }

    private fun createDummyJson(
        phases: List<JsonPhase> = listOf(
            JsonPhase(
                id = "a_phase",
                title = "Phase A",
                order = 0,
                topics = listOf(
                    JsonTopic(
                        id = "a_kotlin_mastery",
                        title = "Kotlin Mastery",
                        path = "content/a_phase/kotlin.md",
                        subtopics = listOf(
                            JsonTopic(
                                id = "a_kotlin_syntax",
                                title = "Kotlin Syntax",
                                path = "content/a_phase/kotlin_syntax.md"
                            )
                        )
                    ),
                    JsonTopic(
                        id = "b_coroutines",
                        title = "Coroutines",
                        path = "content/a_phase/coroutines.md"
                    )
                )
            ),
            JsonPhase(
                id = "b_phase",
                title = "Phase B",
                order = 1,
                topics = listOf(
                    JsonTopic(
                        id = "a_compose",
                        title = "Jetpack Compose",
                        path = "content/b_phase/compose.md"
                    )
                )
            )
        )
    ): String {
        return Json.encodeToString(RoadmapJson.serializer(), RoadmapJson(domain = "kotlin", phases = phases))
    }

    @Test
    fun `getPhases parses topics_list json correctly`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStream: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStream

        val phases = dataSource.getPhases()

        assertEquals(2, phases.size)
        assertEquals("a_phase", phases[0].id)
        assertEquals("Phase A", phases[0].title)
        assertEquals(2, phases[0].topics.size)

        assertEquals("a_kotlin_mastery", phases[0].topics[0].id)
        assertEquals("Kotlin Mastery", phases[0].topics[0].title)
        assertEquals("content/a_phase/kotlin.md", phases[0].topics[0].contentPath)
        assertEquals(1, phases[0].topics[0].subtopics.size)
        assertEquals("a_kotlin_syntax", phases[0].topics[0].subtopics[0].id)
        assertEquals("Kotlin Syntax", phases[0].topics[0].subtopics[0].title)
        assertEquals("content/a_phase/kotlin_syntax.md", phases[0].topics[0].subtopics[0].contentPath)

        assertEquals("b_coroutines", phases[0].topics[1].id)
        assertEquals("Coroutines", phases[0].topics[1].title)
        assertEquals("content/a_phase/coroutines.md", phases[0].topics[1].contentPath)
    }

    @Test
    fun `getTopicContent returns correct content for a given topicId`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        val topicPath = "content/a_phase/kotlin.md"
        val expectedContent = "## Kotlin Mastery Content"
        val inputStreamContent: InputStream = ByteArrayInputStream(expectedContent.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson
        every { mockAssetManager.open(topicPath) } returns inputStreamContent

        // First call getPhases to populate the internal topic map
        dataSource.getPhases()

        val content = dataSource.getTopicContent("a_kotlin_mastery")
        assertEquals(expectedContent, content)
    }

    @Test
    fun `getTopicContent returns null if content path is null`() = runTest {
        val dummyJsonString = createDummyJson(
            phases = listOf(
                JsonPhase(
                    id = "a_phase",
                    title = "Phase A",
                    order = 0,
                    topics = listOf(
                        JsonTopic(
                            id = "topic_without_path",
                            title = "Topic Without Path",
                            path = null // Path is null
                        )
                    )
                )
            )
        )
        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson

        dataSource.getPhases()

        val content = dataSource.getTopicContent("topic_without_path")
        assertNull(content)
    }

    @Test
    fun `getTopicContent returns null if topicId not found`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson

        dataSource.getPhases()

        val content = dataSource.getTopicContent("non_existent_topic")
        assertNull(content)
    }

    @Test
    fun `getTopicContent returns null if content file not found`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        val topicPath = "content/a_phase/kotlin.md" // Path for a_kotlin_mastery
        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson
        every { mockAssetManager.open(topicPath) } throws Exception("File not found") // Simulate file not found

        dataSource.getPhases() // Make sure phases are loaded

        val content = dataSource.getTopicContent("a_kotlin_mastery")
        assertNull(content)
    }
}
