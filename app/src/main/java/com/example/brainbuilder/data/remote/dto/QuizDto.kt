package com.example.brainbuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GradeQuizRequest(
    val quizId: String,
    val answers: Map<String, String>
)

data class GradeQuizResponse(
    val submissionId: String,
    val score: Double,
    val perQuestionCorrectness: List<QuestionResult>,
    val anyIncorrect: Boolean
)

data class QuestionResult(
    val questionId: String,
    val isCorrect: Boolean
)

data class QuestionItem(
    val id: String,
    val type: String,
    val prompt: String,
    val options: List<String>?
)

// One explanation row from PostgREST: GET /rest/v1/explanations?select=question_id,steps
data class ExplanationItem(
    @SerializedName("question_id") val questionId: String,
    val steps: List<String>
)
