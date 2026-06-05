package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ProgressSummaryResponse
import com.example.brainbuilder.data.remote.service.ProgressService
import retrofit2.Response

class ProgressRepository(
    private val service: ProgressService
) {
    suspend fun getProgress(): Response<ProgressSummaryResponse> {
        return service.getProgress()
    }
}
