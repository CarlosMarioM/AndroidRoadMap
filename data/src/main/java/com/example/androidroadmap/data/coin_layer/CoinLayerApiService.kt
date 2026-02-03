package com.example.androidroadmap.data.coin_layer

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinLayerApiService {
    @GET("live")
    suspend fun getLive(
        @Query("access_key") apiKey: String,
        @Query("callback") callback: String?,
    ): String
}