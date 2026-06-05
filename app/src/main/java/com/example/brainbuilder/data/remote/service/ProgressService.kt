package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.ProgressSummary
import retrofit2.Response
import retrofit2.http.GET

interface ProgressService {
    // UC-05 — the backend aggregates completion %, score history, and recommendations server-side.
    @GET("learning-progress")
    suspend fun getProgress(): Response<ApiResponse<ProgressSummary>>
}
