package com.example.chickcheckapp.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {

    fun getSession() = repository.getSession()

    fun getArticle(token: String) = repository.getArticles(token)

    fun getProfile(token: String) = repository.getProfile(token)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun logoutFromApi(token: String): LiveData<Result<LogoutResponse>> =
        repository.logoutFromApi(token)
}