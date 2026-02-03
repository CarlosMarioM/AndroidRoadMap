package com.example.androidroadmap.data.weather.remote.repository

import com.example.androidroadmap.domain.weather.repository.WeatherRepository
import com.example.androidroadmap.data.weather.remote.WeatherApiService
import com.example.androidroadmap.model.weather.CurrentWeatherApiRes
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val apiKey: String
) : WeatherRepository {

    // Placeholder city name, will need to be properly handled (e.g., via Geocoding API)

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<CurrentWeatherApiRes> {
        return try {
            val response = apiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
            )
            // Assuming current weather is always available in the response
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
