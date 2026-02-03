package com.example.androidroadmap.data.weather.remote

import com.example.androidroadmap.model.weather.CurrentWeatherApiRes
import retrofit2.http.GET
import retrofit2.http.Query
interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("exclude") exclude: String? = null, // e.g., "minutely,hourly,daily,alerts"
        @Query("units") units: String? = "metric", // "standard", "metric", "imperial"
        @Query("lang") lang: String? = null, // e.g., "en", "es"
    ): CurrentWeatherApiRes // This will be our main response data class
}