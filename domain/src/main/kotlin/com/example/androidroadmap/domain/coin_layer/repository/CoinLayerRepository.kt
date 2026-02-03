package com.example.androidroadmap.domain.coin_layer.repository

interface CoinLayerRepository {
    suspend fun getLive(callback: String) : Result<String>
}