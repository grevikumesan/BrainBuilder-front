package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.repository.PaymentRepository

class PaymentViewModelFactory(
    private val paymentRepository: PaymentRepository,
    private val dataStore: DataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(paymentRepository, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
