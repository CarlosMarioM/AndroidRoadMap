package com.example.androidroadmap.domain.model.massive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TickerResponse(
    val results: List<TickerResult>,
    val status: String,
    @SerialName("request_id") val requestId: String,
    val count: Int? = null,
    @SerialName("next_url") val nextUrl: String? = null
)

@Serializable
data class TickerResult(
    val ticker: String,
    val name: String,
    val market: String,
    val locale: String,
    @SerialName("primary_exchange") val primaryExchange: String? = null,
    val type: String? = null,
    val active: Boolean,
    @SerialName("currency_name") val currencyName: String? = null,
    @SerialName("last_updated_utc") val lastUpdatedUtc: String? = null
)