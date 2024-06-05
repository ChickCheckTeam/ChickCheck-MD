package com.example.chickcheckapp.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.local.model.UserModel
import com.example.chickcheckapp.data.remote.response.LogoutResponse
import com.example.chickcheckapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {
    fun getSession() = repository.getSession()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun logoutFromApi(token: String): LiveData<Result<LogoutResponse>> = repository.logoutFromApi(token)
}