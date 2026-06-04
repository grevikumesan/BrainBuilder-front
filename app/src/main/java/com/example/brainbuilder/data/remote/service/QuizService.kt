package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ExplanationItem
import com.example.brainbuilder.data.remote.dto.GradeQuizRequest
import com.example.brainbuilder.data.remote.dto.GradeQuizResponse
import com.example.brainbuilder.data.remote.dto.QuizItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QuizService {
    @GET("quiz-grader/quiz")
    suspend fun getQuiz(@Query("lessonId") lessonId: String): Response<QuizItem>

    @POST("quiz-grader/grade")
    suspend fun gradeQuiz(@Body request: GradeQuizRequest): Response<GradeQuizResponse>

    @GET("quiz-grader/explanations")
    suspend fun getExplanations(@Query("ids") ids: String): Response<List<ExplanationItem>>
}