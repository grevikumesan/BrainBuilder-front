package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.dto.ManageUserRequest
import com.example.brainbuilder.data.remote.repository.AdminRepository
import com.example.brainbuilder.ui.uistate.AdminUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun suspendUser(userId: String) {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        viewModelScope.launch {
            try {
                val request = ManageUserRequest(targetId = userId, actionType = "SUSPEND")
                val response = repository.manageUserAction(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            actionSuccessMessage = "User successfully suspended"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, hasError = true, errorMessage = "Failed to suspend user")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, hasError = true, errorMessage = e.message ?: "Network error occurred")
                }
            }
        }
    }

    fun activateUser(userId: String) {
        // TODO(hans): Implement activation API call using $userId
        _uiState.update { it.copy(isLoading = true) }
    }

    fun rejectCourse(courseId: String, reason: String) {
        // TODO(hans): Implement reject API call using $courseId and $reason
        _uiState.update { it.copy(isLoading = true) }
    }
}