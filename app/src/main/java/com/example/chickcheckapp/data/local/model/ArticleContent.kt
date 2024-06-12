package com.example.chickcheckapp.data.local.model

data class ArticleContent(
    var icon:Int,
    val title: String,
    val content: String,
    var isExpanded: Boolean = false
)