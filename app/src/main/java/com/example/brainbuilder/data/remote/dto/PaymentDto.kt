package com.example.brainbuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

// The paying user is identified server-side from the JWT subject, never from the
// request body, so a client cannot pay on another user's behalf (NFR-01, UC-07).
data class CreatePaymentRequest(
    val planId: String
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
