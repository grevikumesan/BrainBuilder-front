package com.example.brainbuilder.data.remote.dto

// Taxonomy enums mirror the backend ENUMs (NFR-10). Gson serializes each entry by name.
enum class Subject { MATHEMATICS, PHYSICS, CHEMISTRY }

enum class Grade { X, XI, XII }

enum class QuestionType { MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER }

data class CreateCourseRequest(
    val title: String,
    val description: String? = null,
    val subject: Subject,
    val grade: Grade,
    val lessons: List<CreateLessonRequest>
)

data class CreateLessonRequest(
    val title: String,
    val videoUrl: String? = null,
    val richTextContent: String? = null,
    val summary: String? = null,
    val isPremium: Boolean,
    val order: Int,
    val quiz: CreateQuizRequest? = null
)

data class CreateQuizRequest(
    val questions: List<CreateQuestionRequest>
)

data class CreateQuestionRequest(
    val type: QuestionType,
    val prompt: String,
    val options: List<String>? = null,
    val correctAnswer: String,
    val explanation: CreateExplanationRequest? = null
)

data class CreateExplanationRequest(
    val steps: List<String>
)

data class CourseData(
    val courseId: String,
    val status: String
)

data class CourseCreateResponse(
    val success: Boolean,
    val data: CourseData? = null,
    val error: String? = null
)
