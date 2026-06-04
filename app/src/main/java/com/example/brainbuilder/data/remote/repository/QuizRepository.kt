package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ExplanationItem
import com.example.brainbuilder.data.remote.dto.GradeQuizResponse
import com.example.brainbuilder.data.remote.dto.QuizItem
import com.example.brainbuilder.data.remote.service.QuizService
import retrofit2.Response

class QuizRepository(
    private val service: QuizService
) {
    suspend fun getQuiz(lessonId: String): Response<QuizItem> {
        return service.getQuiz(lessonId)
    }

    suspend fun gradeQuiz(
        quizId: String,
        answers: Map<String, String>
    ): Response<GradeQuizResponse> {
        return service.gradeQuiz(
            com.example.brainbuilder.data.remote.dto.GradeQuizRequest(
                quizId = quizId,
                answers = answers
            )
        )
    }

    suspend fun getExplanations(incorrectIds: List<String>): Response<List<ExplanationItem>> {
        return service.getExplanations(incorrectIds.joinToString(","))
    }
}