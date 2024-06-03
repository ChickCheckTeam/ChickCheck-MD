package com.example.chickcheckapp.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chickcheckapp.data.local.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val email = stringPreferencesKey("email")
    private val token = stringPreferencesKey("token")
    private val isLogin = booleanPreferencesKey("is_login")

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[email] = user.email
            preferences[token] = user.token
            preferences[isLogin] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[email] ?: "",
                preferences[token] ?: "",
                preferences[isLogin] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}