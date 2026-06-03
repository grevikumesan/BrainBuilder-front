package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.CreatePaymentRequest
import com.example.brainbuilder.data.remote.dto.CreatePaymentResponse
import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.dto.SubscriptionStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PaymentService {
    @POST("payment-create")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<CreatePaymentResponse>

    @GET("payment-create/subscription")
    suspend fun getSubscriptionStatus(): Response<SubscriptionStatus>

    @GET("payment-create/plans")
    suspend fun getPlans(): Response<List<PlanItem>>
}