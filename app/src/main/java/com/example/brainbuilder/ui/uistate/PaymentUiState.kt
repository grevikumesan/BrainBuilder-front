package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.dto.SubscriptionStatus

data class PaymentUiState(
    val isLoading: Boolean = false,
    val plans: List<PlanItem> = emptyList(),
    val subscriptionStatus: SubscriptionStatus? = null,
    val paymentUrl: String? = null,
    val orderId: String? = null,
    // True while polling the subscription endpoint after the Midtrans page closes (NFR-03).
    val isVerifyingPayment: Boolean = false,
    val errorMessage: String? = null,
    val isPaymentSuccess: Boolean = false
)