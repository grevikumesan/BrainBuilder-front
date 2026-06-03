package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.LoginRequest
import com.example.brainbuilder.data.remote.dto.RegisterRequest
import com.example.brainbuilder.data.remote.dto.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth-login/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth-login/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>
}