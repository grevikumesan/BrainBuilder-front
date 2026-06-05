package com.example.brainbuilder.data.remote.dto

// GET /learning-progress (UC-05). Field names are camelCase, matching the backend response.
data class CourseProgress(
    val courseId: String,
    val courseTitle: String,
    val subject: String,
    val grade: String,
    val completionPct: Double,
    val totalLessons: Int,
    val completedLessons: Int,
    val lastAccessedAt: String? = null
)

data class QuizScoreHistory(
    val quizId: String,
    val lessonTitle: String,
    val courseTitle: String,
    val score: Double,
    val submittedAt: String
)

data class RecommendedTopic(
    val courseId: String,
    val courseTitle: String,
    val lessonId: String,
    val lessonTitle: String,
    val avgScore: Double
)

data class ProgressSummary(
    val hasStarted: Boolean = false,
    val courses: List<CourseProgress> = emptyList(),
    val scoreHistory: List<QuizScoreHistory> = emptyList(),
    val recommendedTopics: List<RecommendedTopic> = emptyList()
)
