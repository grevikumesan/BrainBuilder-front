package com.example.brainbuilder.data.remote.dto

data class CourseDetailResponse(
    val id: String,
    val title: String,
    val description: String?,
    val subject: String,
    val grade: String,
    val lessons: List<CourseLesson>
)

// Lighter lesson shape returned by /course-detail. Content fields are optional
// because they are withheld (omitted) for premium-gated lessons.
data class CourseLesson(
    val id: String,
    val title: String,
    val videoUrl: String? = null,
    val richTextContent: String? = null,
    val summary: String? = null,
    val isPremium: Boolean = false,
    val order: Int = 0
)
