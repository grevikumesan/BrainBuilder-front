package com.example.brainbuilder.data.remote.dto

data class CourseListResponse(
    val success: Boolean,
    val data: CourseListData
)

data class CourseListData(
    val courses: List<CourseItem>
)

data class CourseItem(
    val id: String,
    val subject: String,
    val grade: String,
    val title: String,
    val description: String,
    val createdAt: String
)

data class LessonDetailResponse(
    val success: Boolean,
    val data: LessonDetailData
)

data class LessonDetailData(
    val lesson: LessonDetail,
    val accessAllowed: Boolean
)

data class LessonDetail(
    val id: String,
    val courseId: String,
    val title: String,
    val videoUrl: String,
    val richTextContent: String,
    val summary: String,
    val isPremium: Boolean,
    val order: Int,
    val quiz: QuizSummary?
)

data class QuizSummary(
    val id: String,
    val lessonId: String,
    // Questions to render the quiz (UC-03); the answer key is never sent by the backend
    val questions: List<QuestionItem> = emptyList()
)