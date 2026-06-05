package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brainbuilder.data.remote.repository.AdminRepository

class AdminViewModelFactory(
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(adminRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
