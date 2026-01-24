package com.example.androidroadmap.model

import kotlinx.serialization.*

@Serializable
data class Topic(
    val id: TopicId,
    val title: String,
    val path: String
)

@Serializable
@JvmInline
value class TopicId(val id: String){
    companion object {
        fun fromString(string : String) : TopicId {
            return TopicId(string)
        }
    }
}