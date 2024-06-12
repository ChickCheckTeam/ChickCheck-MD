package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("data")
	val data: UserData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("scanHistory")
	val scanHistory: List<ScanHistoryItem?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class ScanHistoryItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("results")
	val results: String? = null,

	@field:SerializedName("suggestion")
	val suggestion: String? = null
)
