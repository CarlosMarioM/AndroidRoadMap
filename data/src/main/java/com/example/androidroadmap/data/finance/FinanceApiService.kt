package com.example.androidroadmap.data.finance

import com.example.androidroadmap.domain.model.massive.AggregatesResponse
import com.example.androidroadmap.domain.model.massive.TickerDetails
import com.example.androidroadmap.domain.model.massive.TickerDetailsResponse
import com.example.androidroadmap.domain.model.massive.TickerResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinanceApiService {
    @GET("v3/reference/tickers")
    suspend fun getTickers(
        @Query("apiKey") apiKey: String, // Massive uses 'apiKey' as the parameter name
        @Query("ticker") ticker: String? = null,
        @Query("type") type: String? = null,
        @Query("market") market: String? = "stocks", // Defaults to stocks as per your docs
        @Query("exchange") exchange: String? = null,
        @Query("cusip") cusip: String? = null,
        @Query("cik") cik: String? = null,
        @Query("date") date: String? = null,
        @Query("search") search: String? = null,
        @Query("active") active: Boolean? = true,
        @Query("order") order: String? = "asc",
        @Query("limit") limit: Int? = 100,
        @Query("sort") sort: String? = "ticker"
    ): TickerResponse

    @GET("v3/reference/tickers/{symbol}")
    suspend fun getTickerDetail(
        @Path("symbol") symbol: String? = "AAPL",
        @Query("apiKey") apiKey: String,
        @Query("date") date: String?
    ): TickerDetailsResponse

    @GET("/v2/aggs/ticker/{stocksTicker}/range/{multiplier}/{timespan}/{from}/{to}")
    suspend fun getAggregateBars(
        @Path("stocksTicker")stocksTicker: String? = "AAPL",
        @Path("multiplier") multiplier: Int? = 1,
        @Path("timespan") timestamp: String? = "",
        @Path("from") from: String? = "",
        @Path("to") to: String? = "",
        @Query("adjusted") adjusted: Boolean = true,
        @Query("sort") sort: String? = "ASC",
        @Query("limit") limit: Int = 5000,
    ) : AggregatesResponse
}