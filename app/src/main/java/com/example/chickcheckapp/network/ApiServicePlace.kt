package com.example.chickcheckapp.network

import com.example.chickcheckapp.BuildConfig
import com.example.chickcheckapp.data.remote.response.NearbyPlaceBodyResponse
import com.example.chickcheckapp.data.remote.response.NearbyPlacesResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServicePlace {
    @POST("https://places.googleapis.com/v1/places:searchNearby")
    suspend fun findNearbyPlaces(
        @Header("X-Goog-Api-Key") apiKey: String = BuildConfig.PLACES_API_KEY,
        @Header("X-Goog-FieldMask") fieldMask: String = "places.displayName,places.formattedAddress,places.photos,places.location,places.googleMapsUri",
        @Body requestBody: NearbyPlaceBodyResponse
    ): NearbyPlacesResponse
}