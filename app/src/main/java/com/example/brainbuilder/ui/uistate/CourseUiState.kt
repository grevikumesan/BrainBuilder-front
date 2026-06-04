package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.CourseItem
import com.example.brainbuilder.data.remote.dto.LessonDetail

data class CourseUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseItem> = emptyList(),
    val selectedLesson: LessonDetail? = null,
    val accessAllowed: Boolean = true,
    val selectedSubject: String = "MATH",
    val selectedGrade: String = "X",
    val errorMessage: String? = null
)