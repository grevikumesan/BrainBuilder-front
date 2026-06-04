package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.CourseListResponse
import com.example.brainbuilder.data.remote.dto.LessonDetailResponse
import com.example.brainbuilder.data.remote.service.CourseService
import retrofit2.Response

class CourseRepository(
    private val service: CourseService
) {

    suspend fun getCourses(subject: String, grade: String): Response<CourseListResponse> {
        return service.getCourses(subject, grade)
    }

    suspend fun getLesson(lessonId: String): Response<LessonDetailResponse> {
        return service.getLesson(lessonId)
    }
}