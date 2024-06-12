package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("data")
	val data: List<ArticleData>,

	@field:SerializedName("status")
	val status: String
)

data class Author(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)

data class ArticleData(

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

	@field:SerializedName("updatedAt")
	val updatedAt: String,

	@field:SerializedName("tags")
	val tags: List<String>
)
