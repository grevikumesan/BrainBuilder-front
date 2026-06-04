package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.CourseDetailResponse

data class CourseDetailUiState(
    val isLoading: Boolean = false,
    val course: CourseDetailResponse? = null,
    val errorMessage: String? = null
)