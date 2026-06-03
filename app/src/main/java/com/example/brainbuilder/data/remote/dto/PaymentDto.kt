package com.example.brainbuilder.data.remote.dto

data class CreatePaymentRequest(
    val planId: String
)

data class CreatePaymentResponse(
    val success: Boolean,
    val data: PaymentData
)

data class PaymentData(
    val paymentUrl: String,
    val orderId: String
)

data class PlanItem(
    val id: String,
    val name: String,
    val price: Double,
    val durationDays: Int
)

data class SubscriptionStatus(
    val id: String,
    val status: String,
    val startDate: String?,
    val expiresAt: String?
)

data class ApiErrorResponse(
    val success: Boolean,
    val error: String
)