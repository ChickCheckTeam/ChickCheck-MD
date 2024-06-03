package com.example.chickcheckapp.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.local.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> = repository.getSession()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}