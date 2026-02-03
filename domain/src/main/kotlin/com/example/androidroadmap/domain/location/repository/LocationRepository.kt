package com.example.androidroadmap.domain.location.repository

import com.example.androidroadmap.model.weather.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location
}