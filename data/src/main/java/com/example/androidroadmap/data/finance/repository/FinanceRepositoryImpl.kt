package com.example.androidroadmap.data.finance.repository

import com.example.androidroadmap.data.finance.FinanceApiService
import com.example.androidroadmap.data.di.MassiveApiKey
import com.example.androidroadmap.domain.model.massive.AggregatesResponse
import com.example.androidroadmap.domain.model.massive.TickerDetailsResponse
import com.example.androidroadmap.domain.model.massive.TickerResponse
import com.example.androidroadmap.domain.repository.finance.repository.FinanceRepository
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val apiService: FinanceApiService,
    private val apiKey: MassiveApiKey
) : FinanceRepository {
    override suspend fun getTickers(limit: Int?): Result<TickerResponse> {
        try {
            val response = apiService.getTickers(
                apiKey = apiKey.value,
            )
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getTickerDetails(
        symbol: String?,
        date: String?
    ): Result<TickerDetailsResponse> {
        try {
            val response = apiService.getTickerDetail(
                symbol = symbol,
                apiKey = apiKey.value,
                date = date,
            )
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getAggregateBars(
        stocksTicker: String?,
        multiplier: Int?,
        timestamp: String?,
        from: String?,
        to: String?,
        adjusted: Boolean?,
        sort: String?,
        limit: Int,
    ): Result<AggregatesResponse> {
        try {
            val response =
                apiService.getAggregateBars(stocksTicker, multiplier, timestamp, from = from, to)
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}

