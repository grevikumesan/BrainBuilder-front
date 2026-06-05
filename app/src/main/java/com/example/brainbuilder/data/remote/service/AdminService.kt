package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ActionResultData
import com.example.brainbuilder.data.remote.dto.AdminItemsData
import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.ManageActionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminService {
    @GET("admin/items")
    suspend fun getItems(): Response<ApiResponse<AdminItemsData>>

    @POST("admin/action")
    suspend fun applyAction(@Body request: ManageActionRequest): Response<ApiResponse<ActionResultData>>
}
