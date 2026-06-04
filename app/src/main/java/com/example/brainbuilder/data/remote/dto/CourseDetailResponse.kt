package com.example.brainbuilder.data.remote.dto

data class CourseDetailResponse(
    val id: String,
    val title: String,
    val description: String?,
    val subject: String,
    val grade: String,
    val lessons: List<LessonDetail>
)