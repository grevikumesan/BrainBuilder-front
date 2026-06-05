package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.dto.ManageActionRequest
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

    fun loadItems() {
        _uiState.update { it.copy(isLoading = true, hasError = false, errorMessage = "") }
        viewModelScope.launch {
            try {
                val response = repository.getItems()
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            users = body.data.users,
                            pendingCourses = body.data.pendingCourses
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessage = body?.error ?: "Failed to load dashboard"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, hasError = true, errorMessage = e.message ?: "Network error occurred")
                }
            }
        }
    }

    fun activateUser(userId: String) = applyAction("ACTIVATE_USER", userId)

    fun suspendUser(userId: String) = applyAction("SUSPEND_USER", userId)

    fun removeUser(userId: String) = applyAction("REMOVE_USER", userId)

    fun approveCourse(courseId: String) = applyAction("APPROVE_COURSE", courseId)

    // reason is required by the backend only for REJECT_COURSE
    fun rejectCourse(courseId: String, reason: String) = applyAction("REJECT_COURSE", courseId, reason)

    private fun applyAction(action: String, targetId: String, reason: String? = null) {
        _uiState.update {
            it.copy(isLoading = true, hasError = false, errorMessage = "", actionSuccessMessage = "")
        }
        viewModelScope.launch {
            try {
                val response = repository.applyAction(ManageActionRequest(targetId, action, reason))
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    _uiState.update {
                        it.copy(actionSuccessMessage = body.data?.message ?: "Action applied successfully")
                    }
                    // Refresh so the list reflects the new user/course state
                    loadItems()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessage = body?.error ?: "Action failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, hasError = true, errorMessage = e.message ?: "Network error occurred")
                }
            }
        }
    }
}
