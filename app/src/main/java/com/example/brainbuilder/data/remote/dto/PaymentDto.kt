package com.example.brainbuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequest(
    val planId: String,
    // Required by the backend validator even though it overrides it with the JWT subject.
    val userId: String
)

data class PaymentData(
    val paymentUrl: String,
    val orderId: String
)

data class PlanItem(
    val id: String,
    val name: String,
    val price: Double,
    @SerializedName("duration_days") val durationDays: Int
)

data class SubscriptionStatus(
    val status: String,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("expires_at") val expiresAt: String? = null
)
