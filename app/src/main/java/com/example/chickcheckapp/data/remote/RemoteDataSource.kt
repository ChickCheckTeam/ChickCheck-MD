package com.example.chickcheckapp.data.remote

import android.util.Log
import com.example.chickcheckapp.data.remote.request.LoginRequest
import com.example.chickcheckapp.data.remote.request.SignUpRequest
import com.example.chickcheckapp.data.remote.response.ArticleResponse
import com.example.chickcheckapp.data.remote.response.DetectionResultResponse
import com.example.chickcheckapp.data.remote.response.LoginResponse
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlaceBodyResponse
import com.example.chickcheckapp.data.remote.response.ProfileResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import com.example.chickcheckapp.network.ApiService
import com.example.chickcheckapp.network.ApiServicePlace
import okhttp3.MultipartBody
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val apiServicePlace: ApiServicePlace
) {
    suspend fun findNearbyPlaces(bodyResponse: NearbyPlaceBodyResponse) = apiServicePlace.findNearbyPlaces(requestBody = bodyResponse)
    suspend fun postDetection(image: MultipartBody.Part,token: String) : DetectionResultResponse {
        val formatToken = "Bearer $token"
        val sessionCookie = "session=$token"
        return apiService.postDetection( image,formatToken,sessionCookie)
    }

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

    suspend fun getArticles(
        token: String
    ):ArticleResponse{
        val formatToken = "Bearer $token"
        val sessionCookie = "session=$token"
        return apiService.getArticles(formatToken, sessionCookie)
    }

    suspend fun login(
        email: String,
        password: String,
    ): LoginResponse {
        val requestBody = LoginRequest(email, password)
        return apiService.login(requestBody)
    }

    suspend fun logout(token: String): LogoutResponse {
        val formatToken = "Bearer $token"
        val sessionCookie = "session=$token"
        return apiService.logout(formatToken, sessionCookie)
    }

    suspend fun getProfile(token: String): ProfileResponse {
        val formatToken = "Bearer $token"
        val sessionCookie = "session=$token"
        return apiService.getProfile(formatToken, sessionCookie)
    }
}