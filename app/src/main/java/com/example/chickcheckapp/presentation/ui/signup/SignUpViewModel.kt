package com.example.chickcheckapp.presentation.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.local.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {
    fun registerUser(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = repository.registerUser(name, username, email, password, confirmPassword)
}