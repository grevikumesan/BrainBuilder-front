package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.CourseCreateResponse
import com.example.brainbuilder.data.remote.dto.CreateCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CourseService {
    @POST("course")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<CourseCreateResponse>
}
