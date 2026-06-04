package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.CourseDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseDetailService {
    @GET("course-detail")
    suspend fun getCourseDetail(
        @Query("courseId") courseId: String
    ): Response<CourseDetailResponse>
}