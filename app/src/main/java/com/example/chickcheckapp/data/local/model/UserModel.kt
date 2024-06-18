package com.example.chickcheckapp.data.local.model

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)