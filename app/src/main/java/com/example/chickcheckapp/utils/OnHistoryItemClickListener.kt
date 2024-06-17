package com.example.chickcheckapp.utils

import com.example.chickcheckapp.data.remote.response.ArticleData

interface OnHistoryItemClickListener {
    fun onHistoryItemClick(article: ArticleData, imageUrl: String)
}