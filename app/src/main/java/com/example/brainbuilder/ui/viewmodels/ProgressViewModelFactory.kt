package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brainbuilder.data.remote.repository.ProgressRepository

class ProgressViewModelFactory(
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(progressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
