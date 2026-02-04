package com.example.androidroadmap.domain.repository.weather.repository

import com.example.androidroadmap.domain.model.weather.CurrentWeatherApiRes

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double) : Result<CurrentWeatherApiRes>
}