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
    // nullable: courses.description is nullable in the DB
    val description: String? = null,
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
    // nullable: these lesson content fields are nullable in the DB and are
    // also blanked/omitted by the backend when premium access is withheld
    val videoUrl: String? = null,
    val richTextContent: String? = null,
    val summary: String? = null,
    val isPremium: Boolean = false,
    val order: Int = 0,
    val quiz: QuizSummary? = null
)

data class QuizSummary(
    val id: String,
    val lessonId: String,
    // Questions to render the quiz (UC-03); the answer key is never sent by the backend
    val questions: List<QuestionItem> = emptyList()
)