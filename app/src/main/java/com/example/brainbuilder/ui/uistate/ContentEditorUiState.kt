package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.Grade
import com.example.brainbuilder.data.remote.dto.QuestionType
import com.example.brainbuilder.data.remote.dto.Subject

// Editable form models for the teacher course editor. Free-text fields keep the UI manageable;
// the ViewModel parses optionsText (comma-separated) and explanationText (one step per line).
data class QuestionForm(
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val prompt: String = "",
    val optionsText: String = "",
    val correctAnswer: String = "",
    val explanationText: String = ""
)

data class LessonForm(
    val title: String = "",
    val videoUrl: String = "",
    val richTextContent: String = "",
    val summary: String = "",
    val isPremium: Boolean = false,
    val questions: List<QuestionForm> = emptyList()
)

data class ContentEditorUiState(
    val title: String = "",
    val description: String = "",
    val subject: Subject = Subject.MATHEMATICS,
    val grade: Grade = Grade.X,
    val lessons: List<LessonForm> = listOf(LessonForm()),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val createdCourseId: String? = null
)
