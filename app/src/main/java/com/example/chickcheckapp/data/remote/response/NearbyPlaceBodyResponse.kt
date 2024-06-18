package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class NearbyPlaceBodyResponse(

    @field:SerializedName("includedTypes")
    val includedTypes: List<String>,

    @field:SerializedName("rankPreference")
    val rankPreference: String,

    @field:SerializedName("locationRestriction")
    val locationRestriction: LocationRestriction,

    @field:SerializedName("maxResultCount")
    val maxResultCount: Int
)

data class Circle(

    @field:SerializedName("center")
    val center: Center,

    @field:SerializedName("radius")
    val radius: Any
)

data class LocationRestriction(

    @field:SerializedName("circle")
    val circle: Circle
)

data class Center(

    @field:SerializedName("latitude")
    val latitude: Any,

    @field:SerializedName("longitude")
    val longitude: Any
)
