package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.dto.ExplanationItem
import com.example.brainbuilder.data.remote.repository.QuizRepository
import com.example.brainbuilder.ui.uistate.QuizUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    fun loadQuiz(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // The quiz (id + questions) arrives inside the lesson detail
                val response = quizRepository.getLesson(lessonId)
                val quiz = response.body()?.data?.lesson?.quiz
                if (response.isSuccessful && quiz != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        quizId = quiz.id,
                        questions = quiz.questions
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load quiz"
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

    fun setAnswer(questionId: String, answer: String) {
        _uiState.value = _uiState.value.copy(
            answers = _uiState.value.answers + (questionId to answer)
        )
    }

    fun gradeQuiz() {
        val quizId = _uiState.value.quizId ?: return
        val answers = _uiState.value.answers

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = quizRepository.gradeQuiz(quizId, answers)
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    val result = body.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSubmitted = true,
                        score = result.score,
                        perQuestionCorrectness = result.perQuestionCorrectness,
                        anyIncorrect = result.anyIncorrect
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to submit quiz"
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

    fun loadExplanations() {
        val incorrectIds = _uiState.value.perQuestionCorrectness
            .filter { !it.isCorrect }
            .map { it.questionId }

        if (incorrectIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingExplanations = true, errorMessage = null)
            try {
                val response = quizRepository.getExplanations(incorrectIds)
                if (response.isSuccessful) {
                    val byQuestion = (response.body() ?: emptyList()).associateBy { it.questionId }
                    // Keep one entry per incorrect question, in order. Questions with
                    // no authored explanation get an empty list so the view can still
                    // page to them and show "Explanation not available yet" (UC-04 Ext 1a)
                    val ordered = incorrectIds.map { questionId ->
                        byQuestion[questionId] ?: ExplanationItem(questionId, emptyList())
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoadingExplanations = false,
                        cachedExplanations = ordered,
                        isShowingExplanations = true,
                        currentExplanationIndex = 0
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingExplanations = false,
                        errorMessage = "Failed to load explanations"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingExplanations = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun showNextExplanation() {
        val current = _uiState.value.currentExplanationIndex
        val total = _uiState.value.cachedExplanations.size
        if (current < total - 1) {
            _uiState.value = _uiState.value.copy(currentExplanationIndex = current + 1)
        }
    }

    fun showPreviousExplanation() {
        val current = _uiState.value.currentExplanationIndex
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentExplanationIndex = current - 1)
        }
    }

    fun closeExplanations() {
        _uiState.value = _uiState.value.copy(
            isShowingExplanations = false,
            currentExplanationIndex = 0
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}