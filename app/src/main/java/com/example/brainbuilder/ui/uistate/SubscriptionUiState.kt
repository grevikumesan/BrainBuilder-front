package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.data.remote.dto.SubscriptionStatusItem

data class SubscriptionUiState(
    val isLoading: Boolean = false,
    val plans: List<PlanItem> = emptyList(),
    val currentSubscription: SubscriptionStatusItem? = null,
    val paymentUrl: String? = null,
    val isPaymentSuccess: Boolean = false,
    val errorMessage: String? = null
)