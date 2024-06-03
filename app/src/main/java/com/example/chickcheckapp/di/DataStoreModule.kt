package com.example.chickcheckapp.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.chickcheckapp.data.local.datastore.UserPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideDatastore(app: Application): DataStore<Preferences> {
        return app.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreference(dataStore: DataStore<Preferences>): UserPreference {
        return UserPreference(dataStore)
    }
}