package com.example.androidroadmap.data.di

import com.example.androidroadmap.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Singleton
data class CoinLayerApiKey(val value:String )
@Singleton
data class WeatherApiKey(val value: String)
data class MassiveApiKey(val value: String)

@Module
@InstallIn(SingletonComponent::class)
object ApiKeysModule {

    @Provides
    @Singleton
    fun provideCoinLayerApiKey(): CoinLayerApiKey {
        return CoinLayerApiKey(BuildConfig.COIN_LAYER_API_KEY)
    }

    @Provides
    @Singleton
    fun provideWeatherApiKey(): WeatherApiKey {
        return WeatherApiKey(BuildConfig.OPEN_WEATHER_API_KEY)
    }

    @Provides
    @Singleton
    fun provideMassiveApiKey(): MassiveApiKey {
        return MassiveApiKey(BuildConfig.MASSIVE_API_KEY)
    }
}