package com.example.chickcheckapp.data.remote

import com.example.chickcheckapp.data.remote.request.SignUpRequest
import com.example.chickcheckapp.data.remote.response.NearbyPlaceBodyResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import com.example.chickcheckapp.network.ApiService
import com.example.chickcheckapp.network.ApiServicePlace
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val apiServicePlace: ApiServicePlace
) {
    suspend fun findNearbyPlaces(bodyResponse: NearbyPlaceBodyResponse) = apiServicePlace.findNearbyPlaces(requestBody = bodyResponse)

    suspend fun registerUser(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): SignupResponse {
        val requestBody = SignUpRequest(name, username, email, password, confirmPassword)
        return apiService.registerUser(requestBody)
    }
}