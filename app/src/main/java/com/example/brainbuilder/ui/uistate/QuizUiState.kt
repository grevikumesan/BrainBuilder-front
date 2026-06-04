package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.ExplanationItem
import com.example.brainbuilder.data.remote.dto.QuestionItem
import com.example.brainbuilder.data.remote.dto.QuestionResult

data class QuizUiState(
    val isLoading: Boolean = false,
    val quizId: String? = null,
    val questions: List<QuestionItem> = emptyList(),
    val answers: Map<String, String> = emptyMap(),
    val isSubmitted: Boolean = false,
    val score: Double? = null,
    val perQuestionCorrectness: List<QuestionResult> = emptyList(),
    val anyIncorrect: Boolean = false,
    val isLoadingExplanations: Boolean = false,
    val cachedExplanations: List<ExplanationItem> = emptyList(),
    val currentExplanationIndex: Int = 0,
    val isShowingExplanations: Boolean = false,
    val errorMessage: String? = null
)