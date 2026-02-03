package com.example.androidroadmap.data.coin_layer.repository

import com.example.androidroadmap.data.coin_layer.CoinLayerApiService
import com.example.androidroadmap.data.di.CoinLayerApiKey
import com.example.androidroadmap.domain.coin_layer.repository.CoinLayerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinLayerRepositoryImpl @Inject constructor(
    private val apiService: CoinLayerApiService,
    private val apiKey: CoinLayerApiKey
) : CoinLayerRepository {
    override fun getLive(callback: String?): Flow<Result<String>> = flow {
        while (true){
            try {
                val response = apiService.getLive(
                    apiKey = apiKey.value,
                    callback = callback
                )
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
            delay(1000)
        }
    }
}

