package com.example.androidroadmap.domain.repository.location.repository

import com.example.androidroadmap.domain.model.weather.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location
}