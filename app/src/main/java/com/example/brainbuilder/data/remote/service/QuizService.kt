package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.GradeQuizRequest
import com.example.brainbuilder.data.remote.dto.GradeQuizResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuizService {
    // UC-03: server is the sole authority on scoring; client only submits answers
    @POST("quiz-grader")
    suspend fun gradeQuiz(@Body request: GradeQuizRequest): Response<ApiResponse<GradeQuizResponse>>
}
