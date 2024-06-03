package com.example.chickcheckapp.network

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("scan")
    suspend fun postDetection(
        @Part image : MultipartBody.Part
    )
}