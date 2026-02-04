package com.example.androidroadmap.domain.repository.finance.repository

import com.example.androidroadmap.domain.model.massive.AggregatesResponse
import com.example.androidroadmap.domain.model.massive.TickerDetailsResponse
import com.example.androidroadmap.domain.model.massive.TickerResponse

interface FinanceRepository {
    suspend fun getTickers(limit: Int? = 100): Result<TickerResponse>
    suspend fun getTickerDetails(symbol: String?, date: String?): Result<TickerDetailsResponse>
    suspend fun getAggregateBars(
        stocksTicker: String?,
        multiplier: Int?,
        timestamp: String?,
        from: String?,
        to: String?,
        adjusted: Boolean?,
        sort: String?,
        limit: Int,
    ): Result<AggregatesResponse>
}