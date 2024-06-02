package com.example.chickcheckapp.network

import com.example.chickcheckapp.data.remote.response.SignupResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("passwordConfirm") confirmPassword: String
    ): SignupResponse

}