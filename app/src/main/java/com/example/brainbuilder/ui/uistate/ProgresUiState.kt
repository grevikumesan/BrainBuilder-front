package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.CourseProgressItem
import com.example.brainbuilder.data.remote.dto.QuizScoreItem
import com.example.brainbuilder.data.remote.dto.RecommendedTopicItem

data class ProgressUiState(
    val isLoading: Boolean = false,
    val hasStarted: Boolean = false,
    val courses: List<CourseProgressItem> = emptyList(),
    val scoreHistory: List<QuizScoreItem> = emptyList(),
    val recommendedTopics: List<RecommendedTopicItem> = emptyList(),
    val hasError: Boolean = false,
    val errorMessage: String = ""
)