package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.ApiResponse
import com.example.brainbuilder.data.remote.dto.ExplanationItem
import com.example.brainbuilder.data.remote.dto.GradeQuizRequest
import com.example.brainbuilder.data.remote.dto.GradeQuizResponse
import com.example.brainbuilder.data.remote.dto.LessonDetailResponse
import com.example.brainbuilder.data.remote.service.CourseService
import com.example.brainbuilder.data.remote.service.ExplanationService
import com.example.brainbuilder.data.remote.service.QuizService
import retrofit2.Response

class QuizRepository(
    private val quizService: QuizService,
    private val courseService: CourseService,
    private val explanationService: ExplanationService
) {
    // The quiz (id + questions) is delivered as part of the lesson detail (UC-02)
    suspend fun getLesson(lessonId: String): Response<LessonDetailResponse> {
        return courseService.getLesson(lessonId)
    }

    suspend fun gradeQuiz(
        quizId: String,
        answers: Map<String, String>
    ): Response<ApiResponse<GradeQuizResponse>> {
        return quizService.gradeQuiz(GradeQuizRequest(quizId = quizId, answers = answers))
    }

    suspend fun getExplanations(questionIds: List<String>): Response<List<ExplanationItem>> {
        return explanationService.getExplanations("in.(${questionIds.joinToString(",")})")
    }
}
