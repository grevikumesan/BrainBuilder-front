package com.example.brainbuilder.data.remote.dto

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

data class ExplanationItem(
    val id: String,
    val questionId: String,
    val steps: List<String>
)

data class QuizItem(
    val id: String,
    val lessonId: String,
    val questions: List<QuestionItem>
)

data class QuestionItem(
    val id: String,
    val type: String,
    val prompt: String,
    val options: List<String>?
)