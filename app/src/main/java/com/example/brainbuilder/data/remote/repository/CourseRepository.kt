package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.CourseCreateResponse
import com.example.brainbuilder.data.remote.dto.CreateCourseRequest
import com.example.brainbuilder.data.remote.service.CourseService
import retrofit2.Response

class CourseRepository(
    private val service: CourseService
) {

    suspend fun createCourse(request: CreateCourseRequest): Response<CourseCreateResponse> {
        return service.createCourse(request)
    }
}
