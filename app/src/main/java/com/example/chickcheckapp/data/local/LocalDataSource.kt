package com.example.chickcheckapp.data.local

import com.example.chickcheckapp.data.local.datastore.UserPreference
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val userPreference: UserPreference
) {
    fun getSession() = userPreference.getSession()
}