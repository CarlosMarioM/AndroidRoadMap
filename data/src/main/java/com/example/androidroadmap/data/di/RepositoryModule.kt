package com.example.androidroadmap.data.di

import com.example.androidroadmap.data.coin_layer.repository.CoinLayerRepositoryImpl
import com.example.androidroadmap.data.weather.remote.repository.WeatherRepositoryImpl
import com.example.androidroadmap.domain.coin_layer.repository.CoinLayerRepository
import com.example.androidroadmap.domain.weather.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCoinLayerRepository(
        coinLayerRepositoryImpl: CoinLayerRepositoryImpl
    ): CoinLayerRepository
}
