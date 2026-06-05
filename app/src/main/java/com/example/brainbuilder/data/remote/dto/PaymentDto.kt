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

// duration_days from the public /payment-create/plans endpoint, durationDays from
// /manage-subscription — accept either key so one model serves both (UC-06).
data class PlanItem(
    val id: String,
    val name: String,
    val price: Double,
    @SerializedName(value = "duration_days", alternate = ["durationDays"]) val durationDays: Int
)

// Current subscription as returned by GET /manage-subscription (UC-06).
data class SubscriptionStatus(
    val status: String,
    val planId: String? = null,
    val planName: String? = null,
    val startDate: String? = null,
    val expiryDate: String? = null
)

// Envelope of GET /manage-subscription: available plans + the student's current status.
data class ManageSubscriptionData(
    val plans: List<PlanItem> = emptyList(),
    val currentSubscription: SubscriptionStatus
)
