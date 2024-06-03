package com.example.chickcheckapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DetectionResultResponse(

	@field:SerializedName("data")
	val data: DataItem,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

@Parcelize
data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("suggestion")
	val suggestion: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("results")
	val results: String
): Parcelable
