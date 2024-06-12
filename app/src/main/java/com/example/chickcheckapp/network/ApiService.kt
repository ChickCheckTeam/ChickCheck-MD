package com.example.chickcheckapp.network

import okhttp3.MultipartBody
import android.view.PixelCopy.Request
import com.example.chickcheckapp.data.remote.request.LoginRequest
import com.example.chickcheckapp.data.remote.request.SignUpRequest
import com.example.chickcheckapp.data.remote.response.ArticleResponse
import com.example.chickcheckapp.data.remote.response.DetectionResultResponse
import com.example.chickcheckapp.data.remote.response.LoginResponse
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import retrofit2.http.Body
import retrofit2.http.Part
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
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


    @Multipart
    @POST("scan")
    suspend fun postDetection(
        @Part image : MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("Cookie") sessionCookie: String
    ): DetectionResultResponse

    @GET("article")
    suspend fun getArticles(
        @Header("Authorization") token: String,
        @Header("Cookie") sessionCookie: String
    ): ArticleResponse
    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): LogoutResponse

}