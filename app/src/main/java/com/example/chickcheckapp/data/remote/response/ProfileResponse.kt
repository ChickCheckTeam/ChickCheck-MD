package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @field:SerializedName("data")
    val data: DataUser,

    @field:SerializedName("status")
    val status: String
)

data class ScanHistoryItem(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("results")
    val results: String,

    @field:SerializedName("imageUrl")
    val imageUrl: String
)

data class DataUser(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("scanHistory")
    val scanHistory: List<ScanHistoryItem>,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("username")
    val username: String
)
