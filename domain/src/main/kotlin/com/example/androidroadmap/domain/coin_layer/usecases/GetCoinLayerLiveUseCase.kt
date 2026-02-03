package com.example.androidroadmap.domain.coin_layer.usecases

import com.example.androidroadmap.domain.coin_layer.repository.CoinLayerRepository
import javax.inject.Inject

class GetCoinLayerLiveUseCase @Inject constructor(
    private val repository: CoinLayerRepository
){
    operator fun invoke(callback: String?) = repository.getLive(callback)

}