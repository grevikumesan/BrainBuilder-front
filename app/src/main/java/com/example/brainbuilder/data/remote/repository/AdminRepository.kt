package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ManageUserRequest
import com.example.brainbuilder.data.remote.dto.ManageUserResponse
import com.example.brainbuilder.data.remote.service.AdminService
import retrofit2.Response

class AdminRepository(
    private val service: AdminService
) {
    // Forwarding the request to the network service
    suspend fun manageUserAction(request: ManageUserRequest): Response<ManageUserResponse> {
        return service.manageUserAction(request)
    }
}