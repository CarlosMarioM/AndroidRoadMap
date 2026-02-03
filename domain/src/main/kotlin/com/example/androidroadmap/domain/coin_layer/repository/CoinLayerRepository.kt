package com.example.androidroadmap.domain.coin_layer.repository

import kotlinx.coroutines.flow.Flow

interface CoinLayerRepository {
    fun getLive(callback: String?): Flow<Result<String>>
}