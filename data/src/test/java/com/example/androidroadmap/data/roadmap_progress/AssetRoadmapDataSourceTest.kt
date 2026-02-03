package com.example.androidroadmap.data.roadmap_progress

import com.example.androidroadmap.data.roadmap_progress.topics.AssetRoadmapDataSource
import com.example.androidroadmap.data.roadmap_progress.topics.utils.TopicsMarkdownUtil
import com.example.androidroadmap.model.Phase
import com.example.androidroadmap.model.Subtopic
import com.example.androidroadmap.model.Topic
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.sql.Date

class AssetRoadmapDataSourceTest {

    @MockK
    lateinit var mockMarkdownUtil: TopicsMarkdownUtil

    private lateinit var json: Json
    private lateinit var dataSource: AssetRoadmapDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        json = Json { ignoreUnknownKeys = true }
        dataSource = AssetRoadmapDataSource(mockMarkdownUtil)
    }

    private fun createDummyJson(
        phases: List<Phase> = listOf(
            Phase(
                id = "a_phase",
                title = "Phase A",
                order = 0,
                topics = listOf(
                    Topic(
                        id = "a_kotlin_mastery",
                        title = "Kotlin Mastery",
                        subtopics = listOf(
                            Subtopic(
                                id = "a_kotlin_syntax",
                                title = "Kotlin Syntax",
                                path = "content/a_phase/kotlin_syntax.md",
                                isCompleted = false,
                                lastAccessedDate = Date.valueOf("2023-01-01"),
                                examples = emptyList(),
                                notes = null
                            )
                        )
                    ),
                    Topic(
                        id = "b_coroutines",
                        title = "Coroutines",
                        subtopics = listOf(
                            Subtopic(
                                id = "bx",
                                title = "B",
                                path = "content/a_phase/kotlin_syntax.md",
                                isCompleted = false,
                                lastAccessedDate = Date.valueOf("2023-01-01"),
                                examples = emptyList(),
                                notes = null
                            )
                        )
                    )
                )
            ),
        )
    ): String {
        return Json.encodeToString(phases)
    }

    @Test
    fun `getPhases parses topics_list json correctly`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStream: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockMarkdownUtil.open("topics_list.json") } returns inputStream

        val phases = dataSource.getPhases()

        assertEquals(2, phases.size)
        assertEquals("a_phase", phases[0].id)
        assertEquals("Phase A", phases[0].title)
        assertEquals(2, phases[0].topics.size)

        assertEquals("a_kotlin_mastery", phases[0].topics[0].id)
        assertEquals("Kotlin Mastery", phases[0].topics[0].title)
        assertEquals("content/a_phase/kotlin.md", phases[0].topics[0].subtopics[0].path)
        assertEquals(1, phases[0].topics[0].subtopics.size)
        assertEquals("a_kotlin_syntax", phases[0].topics[0].subtopics[0].id)
        assertEquals("Kotlin Syntax", phases[0].topics[0].subtopics[0].title)
        assertEquals(
            "content/a_phase/kotlin_syntax.md",
            phases[0].topics[0].subtopics[0].path
        )

        assertEquals("b_coroutines", phases[0].topics[1].id)
        assertEquals("Coroutines", phases[0].topics[1].title)
        assertEquals("content/a_phase/coroutines.md", phases[0].topics[1].subtopics[0].path)
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

        val content = dataSource.getSubtopicContent("a_kotlin_mastery")
        assertEquals(expectedContent, content)
    }

    @Test
    fun `getTopicContent returns null if content path is null`() = runTest {
        val dummyJsonString = createDummyJson(
            phases = listOf(
                Phase(
                    id = "a_phase",
                    title = "Phase A",
                    order = 0,
                    topics = listOf(
                        Topic(
                            id = "topic_without_path",
                            title = "Topic Without Path",
                            subtopics = listOf(
                                Subtopic(
                                    id = "a_kotlin_syntax",
                                    title = "Kotlin Syntax",
                                    path = "",
                                    isCompleted = false,
                                    lastAccessedDate = Date.valueOf("2023-01-01"),
                                    examples = emptyList(),
                                    notes = null
                                )
                            )
                        ),
                    )
                )
            )
        )

        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson

        dataSource.getPhases()

        val content = dataSource.getSubtopicContent("topic_without_path")
        assertNull(content)
    }

    @Test
    fun `getTopicContent returns null if topicId not found`() = runTest {
        val dummyJsonString = createDummyJson()
        val inputStreamJson: InputStream = ByteArrayInputStream(dummyJsonString.toByteArray())

        every { mockAssetManager.open("topics_list.json") } returns inputStreamJson

        dataSource.getPhases()

        val content = dataSource.getSubtopicContent("non_existent_topic")
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

        val content = dataSource.getSubtopicContent("a_kotlin_mastery")
        assertNull(content)
    }
}
