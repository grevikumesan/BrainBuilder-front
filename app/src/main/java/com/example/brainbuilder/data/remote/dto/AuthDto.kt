package com.example.brainbuilder.data.remote.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthData(
    val accessToken: String,
    val role: String,
    val userId: String
)

data class AuthResponse(
    val success: Boolean,
    val data: AuthData
)