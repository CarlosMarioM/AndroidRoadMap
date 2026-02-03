package com.example.androidroadmap.data.di

import android.content.Context
import com.example.androidroadmap.data.BuildConfig
import com.example.androidroadmap.data.coin_layer.CoinLayerApiService
import com.google.net.cronet.okhttptransport.CronetInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chromium.net.CronetEngine
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoinLayerModule {
    private const val BASE_URL = "https://api.coinlayer.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cronetEngine: CronetEngine
    ): OkHttpClient {
        val cronetInterceptor = CronetInterceptor.newBuilder(cronetEngine).build()
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(cronetInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
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
    fun provideCoinLayerApiService(retrofit: Retrofit): CoinLayerApiService {
        return retrofit.create(CoinLayerApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideCronetEngine(@ApplicationContext context: Context): CronetEngine {
        return CronetEngine.Builder(context).enableHttp2(true)   // Optional: enable HTTP/2
            .enableQuic(true)
            .build()
    }

    @Provides
    fun provideCoinLayerApiKey(): CoinLayerApiKey {
        return CoinLayerApiKey(BuildConfig.COIN_LAYER_API_KEY)
    }

    @Singleton
    @JvmInline
    value class CoinLayerApiKey(val value:String )
}