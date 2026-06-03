package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.LoginRequest
import com.example.brainbuilder.data.remote.dto.RegisterRequest
import com.example.brainbuilder.data.remote.dto.AuthResponse
import com.example.brainbuilder.data.remote.service.AuthService
import retrofit2.Response

class AuthRepository(
    private val service: AuthService
) {
    suspend fun registerUser(request: RegisterRequest): Response<AuthResponse> {
        return service.registerUser(request)
    }

    suspend fun loginUser(request: LoginRequest): Response<AuthResponse> {
        return service.loginUser(request)
    }
}