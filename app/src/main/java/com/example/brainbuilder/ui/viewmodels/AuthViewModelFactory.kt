package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.repository.AuthRepository

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}