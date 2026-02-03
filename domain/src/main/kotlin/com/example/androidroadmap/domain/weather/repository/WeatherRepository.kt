package com.example.androidroadmap.domain.weather.repository

import com.example.androidroadmap.model.weather.CurrentWeatherApiRes

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double) : Result<CurrentWeatherApiRes>
}