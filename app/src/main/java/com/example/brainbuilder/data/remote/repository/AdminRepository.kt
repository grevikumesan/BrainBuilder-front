package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ActionResultData
import com.example.brainbuilder.data.remote.dto.AdminItemsData
import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.ManageActionRequest
import com.example.brainbuilder.data.remote.service.AdminService
import retrofit2.Response

class AdminRepository(
    private val service: AdminService
) {
    suspend fun getItems(): Response<ApiResponse<AdminItemsData>> {
        return service.getItems()
    }

    suspend fun applyAction(request: ManageActionRequest): Response<ApiResponse<ActionResultData>> {
        return service.applyAction(request)
    }
}
