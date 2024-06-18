package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("data")
	val data: UserData,

	@field:SerializedName("status")
	val status: String
)

data class UserData(

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

data class Article(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("sources")
	val sources: List<String>,

	@field:SerializedName("author")
	val author: Author,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("tags")
	val tags: List<String>,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class ScanHistoryItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("results")
	val results: String,

	@field:SerializedName("article")
	val article: Article
)
