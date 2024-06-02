package com.example.chickcheckapp.di

import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.remote.RemoteDataSource
import com.example.chickcheckapp.network.ApiService
import com.example.chickcheckapp.network.ApiServicePlace
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService,apiServicePlace: ApiServicePlace): RemoteDataSource {
        return RemoteDataSource(apiService,apiServicePlace)
    }

    @Provides
    @Singleton
    fun provideRepository(remoteDataSource: RemoteDataSource): ChickCheckRepository {
        return ChickCheckRepository(remoteDataSource)
    }

}