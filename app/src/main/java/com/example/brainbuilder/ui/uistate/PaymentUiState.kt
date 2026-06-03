package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.dto.SubscriptionStatus

data class PaymentUiState(
    val isLoading: Boolean = false,
    val plans: List<PlanItem> = emptyList(),
    val subscriptionStatus: SubscriptionStatus? = null,
    val paymentUrl: String? = null,
    val orderId: String? = null,
    val errorMessage: String? = null,
    val isPaymentSuccess: Boolean = false
)