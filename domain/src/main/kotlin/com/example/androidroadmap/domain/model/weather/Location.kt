package com.example.androidroadmap.domain.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Long, // OpenWeather city ID
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val state: String? = null // For US states or similar
)
