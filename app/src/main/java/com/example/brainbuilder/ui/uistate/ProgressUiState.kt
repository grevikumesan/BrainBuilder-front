package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.CourseProgress
import com.example.brainbuilder.data.remote.dto.QuizScoreHistory
import com.example.brainbuilder.data.remote.dto.RecommendedTopic

data class ProgressUiState(
    val isLoading: Boolean = false,
    val hasStarted: Boolean = false,
    val courses: List<CourseProgress> = emptyList(),
    val scoreHistory: List<QuizScoreHistory> = emptyList(),
    val recommendedTopics: List<RecommendedTopic> = emptyList(),
    val errorMessage: String? = null
)
