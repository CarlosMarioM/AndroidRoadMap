package com.example.androidroadmap.domain.weather.usecases

import com.example.androidroadmap.domain.weather.repository.WeatherRepository
import com.example.androidroadmap.model.weather.CurrentWeatherApiRes
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double) : Result<CurrentWeatherApiRes> {
        return repository.getCurrentWeather(latitude, longitude)
    }
}