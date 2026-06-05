package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.CreatePaymentRequest
import com.example.brainbuilder.data.remote.dto.ManageSubscriptionData
import com.example.brainbuilder.data.remote.dto.PaymentData
import com.example.brainbuilder.data.remote.dto.PlanItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PaymentService {
    @POST("payment-create")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<ApiResponse<PaymentData>>

    // UC-06 — plans + the student's current subscription status in one call.
    @GET("manage-subscription")
    suspend fun getManageSubscription(): Response<ApiResponse<ManageSubscriptionData>>

    // Public pricing list (no auth required, FR-08).
    @GET("payment-create/plans")
    suspend fun getPlans(): Response<ApiResponse<List<PlanItem>>>
}
