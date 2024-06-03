package com.example.chickcheckapp.data.remote.request

data class SignUpRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val passwordConfirm: String
)

