package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ProgressSummaryResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProgressService {
    @GET("track-progress")
    suspend fun getProgress(): Response<ProgressSummaryResponse>
}