package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.CourseDetailResponse
import com.example.brainbuilder.data.remote.service.CourseDetailService
import retrofit2.Response

class CourseDetailRepository(
    private val service: CourseDetailService
) {
    suspend fun getCourseDetail(courseId: String): Response<CourseDetailResponse> {
        return service.getCourseDetail(courseId)
    }
}