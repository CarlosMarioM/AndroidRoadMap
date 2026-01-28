package com.example.androidroadmap.features.examples

import android.content.Context
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.InputStreamReader

// Data classes to match the structure of phases.json
@Serializable
data class PhaseTopicJson(
    val id: String,
    val title: String,
    val path: String,
    val examples: List<ExampleJson>? = null // New field to hold examples
)

@Serializable
data class ExampleJson(
    val id: String,
    val title: String,
    val description: String,
    val contentKey: String
)

object ExampleRegistry {
    private lateinit var _examples: List<ExampleInfo>
    val examples: List<ExampleInfo>
        get() = _examples

    private val composableMap: MutableMap<String, @Composable () -> Unit> = mutableMapOf()

    fun registerComposable(key: String, composable: @Composable () -> Unit) {
        composableMap[key] = composable
    }

    fun getComposableForKey(key: String): (@Composable () -> Unit)? {
        return composableMap[key]
    }

    fun initialize(context: Context) {
        val jsonString = loadJsonFromAsset(context, "content/phases.json")
        val phaseTopics = Json.decodeFromString<List<PhaseTopicJson>>(jsonString)

        _examples = phaseTopics.flatMap { topic ->
            topic.examples?.map { exampleJson ->
                ExampleInfo(
                    id = exampleJson.id,
                    title = exampleJson.title,
                    description = exampleJson.description,
                    contentKey = exampleJson.contentKey
                )
            } ?: emptyList()
        }
    }

    private fun loadJsonFromAsset(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "{}" // Return an empty JSON object or handle error appropriately
        }
    }

    // TODO: This part will be populated when we refactor example files.
    // For now, we'll keep the `composableMap` empty, and populate it later.
}
