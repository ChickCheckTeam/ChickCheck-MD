package com.example.chickcheckapp.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.chickcheckapp.data.local.LocalDataSource
import com.example.chickcheckapp.data.local.model.UserModel
import com.example.chickcheckapp.data.remote.RemoteDataSource
import com.example.chickcheckapp.data.remote.request.SignUpRequest
import com.example.chickcheckapp.data.remote.response.Center
import com.example.chickcheckapp.data.remote.response.Circle
import com.example.chickcheckapp.data.remote.response.LocationRestriction
import com.example.chickcheckapp.data.remote.response.LoginResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlaceBodyResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlacesResponse
import com.example.chickcheckapp.data.remote.response.SignupResponse
import com.example.chickcheckapp.utils.Result
import okhttp3.RequestBody
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
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
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
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        localDataSource.saveSession(user)
    }

    suspend fun logout() {
        localDataSource.logout()
    }
}