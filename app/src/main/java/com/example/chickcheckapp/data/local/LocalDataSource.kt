package com.example.chickcheckapp.data.local

import com.example.chickcheckapp.data.local.datastore.UserPreference
import com.example.chickcheckapp.data.local.model.UserModel
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val userPreference: UserPreference
) {
    fun getSession() = userPreference.getSession()

    suspend fun saveSession(user: UserModel) = userPreference.saveSession(user)

    suspend fun logout() = userPreference.logout()

}