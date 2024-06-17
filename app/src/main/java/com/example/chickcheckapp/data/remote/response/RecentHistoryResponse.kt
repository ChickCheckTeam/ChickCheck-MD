package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RecentHistoryResponse(

	@field:SerializedName("data")
	val data: List<ScanDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class ArticleDataItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("sources")
	val sources: List<String>,

	@field:SerializedName("author")
	val author: AuthorHistory,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String,

	@field:SerializedName("tags")
	val tags: List<String>
)

data class ScanDataItem(

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

data class AuthorHistory(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
