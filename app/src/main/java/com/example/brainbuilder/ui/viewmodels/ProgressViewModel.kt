package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.repository.ProgressRepository
import com.example.brainbuilder.ui.uistate.ProgressUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = progressRepository.getProgress()
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    val data = body.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasStarted = data.hasStarted,
                        courses = data.courses,
                        scoreHistory = data.scoreHistory,
                        recommendedTopics = data.recommendedTopics
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = body?.error ?: "Failed to load progress"
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
}
