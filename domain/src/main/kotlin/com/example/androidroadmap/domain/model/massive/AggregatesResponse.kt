package com.example.androidroadmap.domain.model.massive

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AggregatesResponse(
    val ticker: String,
    val results: List<StockBar>,
    val status: String,
    @SerialName("request_id") val requestId: String,
    val resultsCount: Int
)

@Serializable
data class StockBar(
    @SerialName("o") val open: Double,
    @SerialName("c") val close: Double,
    @SerialName("h") val high: Double,
    @SerialName("l") val low: Double,
    @SerialName("v") val volume: Double,
    @SerialName("t") val timestamp: Long, // Unix Msec
    @SerialName("n") val transactions: Int? = null
)