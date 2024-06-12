package com.example.chickcheckapp.presentation.ui.article

import androidx.lifecycle.ViewModel
import com.example.chickcheckapp.data.ChickCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val repository: ChickCheckRepository) : ViewModel() {
    fun getArticles(token: String) = repository.getArticles(token)
    fun getSession() = repository.getSession()

}