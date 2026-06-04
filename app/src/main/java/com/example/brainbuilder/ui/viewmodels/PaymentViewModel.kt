package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.dto.SubscriptionStatus
import com.example.brainbuilder.data.remote.repository.PaymentRepository
import com.example.brainbuilder.ui.uistate.PaymentUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val paymentRepository: PaymentRepository,
    private val dataStore: DataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = paymentRepository.getPlans()
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        plans = body.data ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = body?.error ?: "Failed to load plans"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun loadSubscriptionStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val status = fetchSubscription()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    subscriptionStatus = status,
                    errorMessage = if (status == null) "Failed to load subscription status" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun createPayment(planId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userId = dataStore.getUserId()
                val response = paymentRepository.createPayment(planId, userId)
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentUrl = body.data.paymentUrl,
                        orderId = body.data.orderId
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = body?.error ?: "Failed to create payment"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun handlePaymentResult(isSuccess: Boolean) {
        _uiState.value = _uiState.value.copy(paymentUrl = null)
        if (isSuccess) {
            pollSubscriptionUntilActive()
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Payment was not completed")
        }
    }

    /**
     * Activation happens server-side via the Midtrans webhook, so after the payment page closes
     * we poll the subscription endpoint until it flips to ACTIVE (or we give up after a timeout).
     */
    private fun pollSubscriptionUntilActive() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifyingPayment = true, errorMessage = null)
            var attempt = 0
            while (attempt < MAX_POLL_ATTEMPTS) {
                val status = fetchSubscription()
                if (status != null) {
                    _uiState.value = _uiState.value.copy(subscriptionStatus = status)
                    if (status.status == "ACTIVE") {
                        _uiState.value = _uiState.value.copy(
                            isVerifyingPayment = false,
                            isPaymentSuccess = true
                        )
                        return@launch
                    }
                }
                attempt++
                delay(POLL_INTERVAL_MS)
            }
            _uiState.value = _uiState.value.copy(
                isVerifyingPayment = false,
                errorMessage = "Payment is still being confirmed. Please refresh in a moment."
            )
        }
    }

    private suspend fun fetchSubscription(): SubscriptionStatus? {
        return try {
            val response = paymentRepository.getSubscriptionStatus()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) body.data else null
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    companion object {
        private const val MAX_POLL_ATTEMPTS = 10
        private const val POLL_INTERVAL_MS = 2000L
    }
}
