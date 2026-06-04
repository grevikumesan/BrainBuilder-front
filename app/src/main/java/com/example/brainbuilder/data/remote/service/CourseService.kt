package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.CourseListResponse
import com.example.brainbuilder.data.remote.dto.LessonDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseService {

    @GET("course-access/courses")
    suspend fun getCourses(
        @Query("subject") subject: String,
        @Query("grade") grade: String
    ): Response<CourseListResponse>

    @GET("course-access/lessons/{id}")
    suspend fun getLesson(
        @Path("id") lessonId: String
    ): Response<LessonDetailResponse>
}