package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ManageUserRequest
import com.example.brainbuilder.data.remote.dto.ManageUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AdminService {
    @POST("admin/manage-user")
    suspend fun manageUserAction(@Body request: ManageUserRequest): Response<ManageUserResponse>

    // TODO(hans): Add endpoint for fetching user and course lists later
}