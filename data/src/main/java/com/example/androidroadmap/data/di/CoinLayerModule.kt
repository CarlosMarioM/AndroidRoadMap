package com.example.androidroadmap.data.di

import com.example.androidroadmap.data.BuildConfig
import com.example.androidroadmap.data.coin_layer.CoinLayerApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoinLayerNetwork

@Module
@InstallIn(SingletonComponent::class)
object CoinLayerModule {
    private const val BASE_URL = "https://api.coinlayer.com/"
    @Provides
    @Singleton
    @CoinLayerNetwork
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
        return Retrofit.Builder().baseUrl(CoinLayerModule.BASE_URL).client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType)).build()
    }

    @Provides
    @Singleton
    fun provideCoinLayerApiService(@CoinLayerNetwork retrofit: Retrofit): CoinLayerApiService {
        return retrofit.create(CoinLayerApiService::class.java)
    }
}
