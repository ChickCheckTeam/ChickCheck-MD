package com.example.chickcheckapp.di

import com.example.chickcheckapp.BuildConfig
import com.example.chickcheckapp.network.ApiService
import com.example.chickcheckapp.network.ApiServicePlace
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IlZFSkxTaUlmYXlDMGRzN2ZPUFQzIiwiaWF0IjoxNzE3NDE5Njg1fQ.u0dYA5Gz3-BDxhZG0xOSVdyt8buNX7Y8wkCbsOT0Dmk")
                .build()
            chain.proceed(requestHeaders)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    @Named("retrofitApp")
    fun provideRetrofitApp(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }
    @Provides
    @Singleton
    @Named("retrofitPlaces")
    fun provideRetrofitPlaces(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_PLACES)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiServiceApp(@Named("retrofitApp")retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
    @Provides
    @Singleton
    fun provideApiServiceGMaps(@Named("retrofitPlaces")retrofit: Retrofit): ApiServicePlace = retrofit.create(ApiServicePlace::class.java)
}