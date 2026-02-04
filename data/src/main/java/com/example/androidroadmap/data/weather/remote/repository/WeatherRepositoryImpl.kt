package com.example.androidroadmap.data.weather.remote.repository

import com.example.androidroadmap.data.di.WeatherApiKey
import com.example.androidroadmap.domain.repository.weather.repository.WeatherRepository
import com.example.androidroadmap.data.weather.remote.WeatherApiService
import com.example.androidroadmap.domain.model.weather.CurrentWeatherApiRes
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val apiKey: WeatherApiKey
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<CurrentWeatherApiRes> {
        return try {
            val response = apiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey.value,
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
