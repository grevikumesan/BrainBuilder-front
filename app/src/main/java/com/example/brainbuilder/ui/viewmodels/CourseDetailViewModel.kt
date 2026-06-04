package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.repository.CourseDetailRepository
import com.example.brainbuilder.ui.uistate.CourseDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val repository: CourseDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState

    fun loadCourseDetail(courseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            fetchCourseDetailSafe(courseId)
        }
    }

    // Extracted to keep function size under 30 lines
    private suspend fun fetchCourseDetailSafe(courseId: String) {
        try {
            val response = repository.getCourseDetail(courseId)
            if (response.isSuccessful) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    course = response.body()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load course details"
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

class CourseDetailViewModelFactory(
    private val repository: CourseDetailRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}