package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.CreatePaymentResponse
import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.dto.SubscriptionStatus
import com.example.brainbuilder.data.remote.service.PaymentService
import retrofit2.Response

class PaymentRepository(
    private val service: PaymentService
) {

    suspend fun createPayment(planId: String): Response<CreatePaymentResponse> {
        return service.createPayment(
            com.example.brainbuilder.data.remote.dto.CreatePaymentRequest(planId)
        )
    }

    suspend fun getSubscriptionStatus(): Response<SubscriptionStatus> {
        return service.getSubscriptionStatus()
    }

    suspend fun getPlans(): Response<List<PlanItem>> {
        return service.getPlans()
    }
}