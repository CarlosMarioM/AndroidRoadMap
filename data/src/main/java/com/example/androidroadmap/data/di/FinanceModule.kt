package com.example.androidroadmap.data.di

import com.example.androidroadmap.data.finance.FinanceApiService
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
annotation class FinanceNetwork

@Module
@InstallIn(SingletonComponent::class)
object FinanceModule {
    private const val BASE_URL = "https://api.massive.com/"
    @Provides
    @Singleton
    @FinanceNetwork
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
        return Retrofit.Builder().baseUrl(FinanceModule.BASE_URL).client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType)).build()
    }

    @Provides
    @Singleton
    fun provideCoinLayerApiService(@FinanceNetwork retrofit: Retrofit): FinanceApiService {
        return retrofit.create(FinanceApiService::class.java)
    }
}
