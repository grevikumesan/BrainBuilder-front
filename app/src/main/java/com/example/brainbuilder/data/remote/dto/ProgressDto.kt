package com.example.brainbuilder.data.remote.dto

data class CourseProgressItem(
    val courseId: String,
    val courseTitle: String,
    val subject: String,
    val grade: String,
    val completionPct: Int,
    val totalLessons: Int,
    val completedLessons: Int,
    val lastAccessedAt: String?
)

data class QuizScoreItem(
    val quizId: String,
    val lessonTitle: String,
    val courseTitle: String,
    val score: Double,
    val submittedAt: String
)

data class RecommendedTopicItem(
    val courseId: String,
    val courseTitle: String,
    val lessonId: String,
    val lessonTitle: String,
    val avgScore: Int
)

// Mirrors ProgressSummary in track-progress/types.ts
data class ProgressSummaryResponse(
    val hasStarted: Boolean,
    val courses: List<CourseProgressItem>,
    val scoreHistory: List<QuizScoreItem>,
    val recommendedTopics: List<RecommendedTopicItem>
)