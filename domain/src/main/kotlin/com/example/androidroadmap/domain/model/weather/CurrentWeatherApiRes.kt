package com.example.androidroadmap.domain.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherApiRes(
    val coord: CoordResponse,
    val weather: List<WeatherDetailsResponse>,
    val base: String,
    val main: MainResponse,
    val visibility: Int,
    val wind: WindResponse,
    val rain: RainCurrentResponse? = null,
    val clouds: CloudsResponse,
    val dt: Long,
    val sys: SysResponse,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Int
)

@Serializable
data class WeatherDetailsResponse(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class CoordResponse(
    val lon: Double,
    val lat: Double
)

@Serializable
data class MainResponse(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val grndLevel: Int? = null
)

@Serializable
data class WindResponse(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class RainCurrentResponse(
    @SerialName("1h")
    val oneHour: Double? = null
)

@Serializable
data class CloudsResponse(
    val all: Int
)

@Serializable
data class SysResponse(
    val type: Int? = null,
    val id: Long? = null,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
