package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.CreatePaymentRequest
import com.example.brainbuilder.data.remote.dto.ManageSubscriptionData
import com.example.brainbuilder.data.remote.dto.PaymentData
import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.service.PaymentService
import retrofit2.Response

class PaymentRepository(
    private val service: PaymentService
) {

    suspend fun createPayment(planId: String): Response<ApiResponse<PaymentData>> {
        return service.createPayment(CreatePaymentRequest(planId))
    }

    suspend fun getManageSubscription(): Response<ApiResponse<ManageSubscriptionData>> {
        return service.getManageSubscription()
    }

    suspend fun getPlans(): Response<ApiResponse<List<PlanItem>>> {
        return service.getPlans()
    }
}
