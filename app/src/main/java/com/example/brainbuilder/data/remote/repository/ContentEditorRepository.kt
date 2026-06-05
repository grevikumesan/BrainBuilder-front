package com.example.brainbuilder.data.remote.repository

import com.example.brainbuilder.data.remote.dto.CourseCreateResponse
import com.example.brainbuilder.data.remote.dto.CreateCourseRequest
import com.example.brainbuilder.data.remote.service.ContentEditorService
import retrofit2.Response

class ContentEditorRepository(
    private val service: ContentEditorService
) {

    suspend fun createCourse(request: CreateCourseRequest): Response<CourseCreateResponse> {
        return service.createCourse(request)
    }
}
