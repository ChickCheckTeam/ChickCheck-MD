package com.example.chickcheckapp.di

import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.remote.RemoteDataSource
import com.example.chickcheckapp.network.ApiService
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
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideRepository(remoteDataSource: RemoteDataSource): ChickCheckRepository {
        return ChickCheckRepository(remoteDataSource)
    }

}