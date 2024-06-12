package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetectionResultResponse(

	@field:SerializedName("data")
	val data: DataItem,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("article")
	val article: ArticleData,

	@field:SerializedName("status")
	val status: String
)

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("results")
	val results: String
)


