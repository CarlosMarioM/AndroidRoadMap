package com.example.androidroadmap.data.coin_layer.repository

import com.example.androidroadmap.data.coin_layer.CoinLayerApiService
import com.example.androidroadmap.domain.coin_layer.repository.CoinLayerRepository
import javax.inject.Inject

class CoinLayerRepositoryImpl @Inject constructor(
    private val apiService: CoinLayerApiService,
    private val apiKey: String
) : CoinLayerRepository {
    override suspend fun getLive(callback: String): Result<String> {
        return try {
            val response = apiService.getLive(
                apiKey = apiKey,
                callback = callback
            )
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}

