package com.example.chickcheckapp.presentation.ui.profile

import androidx.lifecycle.ViewModel
import com.example.chickcheckapp.data.ChickCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {

    fun getSession() = repository.getSession()

    fun getArticle(token: String) = repository.getArticles(token)

    fun getProfile(token: String) = repository.getProfile(token)
}