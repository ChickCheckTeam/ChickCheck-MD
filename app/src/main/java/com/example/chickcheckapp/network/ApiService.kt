package com.example.chickcheckapp.network

import com.example.chickcheckapp.data.remote.request.LoginRequest
import com.example.chickcheckapp.data.remote.request.SignUpRequest
import com.example.chickcheckapp.data.remote.response.LoginResponse
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun registerUser(
        @Body requestBody: SignUpRequest
    ): SignupResponse

    @POST("login")
    suspend fun login(
        @Body requestBody: LoginRequest
    ): LoginResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): LogoutResponse

}