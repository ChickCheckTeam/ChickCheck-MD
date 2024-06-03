package com.example.chickcheckapp.data

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.chickcheckapp.data.local.LocalDataSource
import com.example.chickcheckapp.data.local.model.UserModel
import com.example.chickcheckapp.data.remote.RemoteDataSource
import com.example.chickcheckapp.data.remote.response.Center
import com.example.chickcheckapp.data.remote.response.Circle
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.data.remote.response.LocationRestriction
import com.example.chickcheckapp.data.remote.response.LoginResponse
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlaceBodyResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlacesResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import com.example.chickcheckapp.utils.Result
import com.example.chickcheckapp.utils.Utils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class ChickCheckRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    fun getSession(): LiveData<UserModel> {
        return localDataSource.getSession().asLiveData()
    }

    fun findNearbyPlaces(location:Location) : LiveData<Result<NearbyPlacesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val center = Center(location.latitude,location.longitude)
            val radius = 5000
            val circle = Circle(center,radius)
            val body = NearbyPlaceBodyResponse(
                includedTypes = listOf("veterinary_care"),
                locationRestriction = LocationRestriction(circle),
                maxResultCount = 10,
                rankPreference = "DISTANCE"
            )
            val response = remoteDataSource.findNearbyPlaces(body)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.e(TAG, "findNearbyPlaces: ${e.message.toString()}")
        }
    }

    fun postDetection(file: File,token:String):LiveData<Result<DataItem>> = liveData{
        emit(Result.Loading)
        try {
            val requestImageFile = file.asRequestBody("image/jpg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestImageFile
            )
            val requestToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IlZFSkxTaUlmYXlDMGRzN2ZPUFQzIiwiaWF0IjoxNzE3NDE5Njg1fQ.u0dYA5Gz3-BDxhZG0xOSVdyt8buNX7Y8wkCbsOT0Dmk"
            Log.d(TAG,requestToken)
            val response = remoteDataSource.postDetection(multipartBody,token=requestToken)
            emit(Result.Success(response.data))
        } catch (e: HttpException) {
            val errorMessage = Utils.parseJsonToErrorMessage(e.response()?.errorBody()?.string())
            emit(Result.Error(errorMessage))
            Log.e(TAG, "postDetection: $errorMessage")
        }
    }

    fun registerUser(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.registerUser(name, username, email, password, confirmPassword)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorMessage = Utils.parseJsonToErrorMessage(e.response()?.errorBody()?.string())
            emit(Result.Error(errorMessage))
            Log.e(TAG, "Register: $errorMessage")
        }
    }

    fun login(
        username: String,
        password: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.login(username, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorMessage = Utils.parseJsonToErrorMessage(e.response()?.errorBody()?.string())
            emit(Result.Error(errorMessage))
            Log.e(TAG, "Login: $errorMessage")
        }
    }

    suspend fun saveSession(user: UserModel) {
        localDataSource.saveSession(user)
    }

    suspend fun logout() {
        localDataSource.logout()
    }

    companion object {
        const val TAG = "ChickCheckRepository"
    }
    fun logoutFromApi(token: String): LiveData<Result<LogoutResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.logout(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}