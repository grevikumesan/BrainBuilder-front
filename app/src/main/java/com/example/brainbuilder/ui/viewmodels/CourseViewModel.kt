package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.remote.dto.CreateCourseRequest
import com.example.brainbuilder.data.remote.dto.CreateExplanationRequest
import com.example.brainbuilder.data.remote.dto.CreateLessonRequest
import com.example.brainbuilder.data.remote.dto.CreateQuestionRequest
import com.example.brainbuilder.data.remote.dto.CreateQuizRequest
import com.example.brainbuilder.data.remote.dto.Grade
import com.example.brainbuilder.data.remote.dto.QuestionType
import com.example.brainbuilder.data.remote.dto.Subject
import com.example.brainbuilder.data.remote.repository.CourseRepository
import com.example.brainbuilder.ui.uistate.CourseUiState
import com.example.brainbuilder.ui.uistate.LessonForm
import com.example.brainbuilder.ui.uistate.QuestionForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }

    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }

    fun selectSubject(subject: Subject) = _uiState.update { it.copy(subject = subject) }

    fun selectGrade(grade: Grade) = _uiState.update { it.copy(grade = grade) }

    fun addLesson() = _uiState.update { it.copy(lessons = it.lessons + LessonForm()) }

    fun removeLesson(index: Int) = _uiState.update { state ->
        state.copy(lessons = state.lessons.filterIndexed { i, _ -> i != index })
    }

    fun updateLesson(index: Int, transform: (LessonForm) -> LessonForm) = _uiState.update { state ->
        state.copy(lessons = state.lessons.mapIndexed { i, lesson -> if (i == index) transform(lesson) else lesson })
    }

    fun addQuestion(lessonIndex: Int) = updateLesson(lessonIndex) {
        it.copy(questions = it.questions + QuestionForm())
    }

    fun removeQuestion(lessonIndex: Int, questionIndex: Int) = updateLesson(lessonIndex) { lesson ->
        lesson.copy(questions = lesson.questions.filterIndexed { i, _ -> i != questionIndex })
    }

    fun updateQuestion(
        lessonIndex: Int,
        questionIndex: Int,
        transform: (QuestionForm) -> QuestionForm
    ) = updateLesson(lessonIndex) { lesson ->
        lesson.copy(questions = lesson.questions.mapIndexed { i, q -> if (i == questionIndex) transform(q) else q })
    }

    fun submit() {
        val validationError = validate()
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
                val response = courseRepository.createCourse(buildRequest(_uiState.value))
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    _uiState.update { it.copy(isSubmitting = false, createdCourseId = body.data.courseId) }
                } else {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = body?.error ?: "Failed to create course") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, errorMessage = e.message) }
            }
        }
    }

    fun startNewCourse() {
        _uiState.value = CourseUiState()
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private fun validate(): String? {
        val state = _uiState.value
        if (state.title.isBlank()) return "Course title is required"
        if (state.lessons.isEmpty()) return "Add at least one lesson"
        state.lessons.forEachIndexed { i, lesson ->
            if (lesson.title.isBlank()) return "Lesson ${i + 1}: title is required"
            lesson.questions.forEachIndexed { j, question ->
                if (question.prompt.isBlank()) return "Lesson ${i + 1} Question ${j + 1}: prompt is required"
                if (question.correctAnswer.isBlank()) return "Lesson ${i + 1} Question ${j + 1}: correct answer is required"
            }
        }
        return null
    }

    private fun buildRequest(state: CourseUiState): CreateCourseRequest {
        val lessons = state.lessons.mapIndexed { index, lesson ->
            CreateLessonRequest(
                title = lesson.title.trim(),
                videoUrl = lesson.videoUrl.ifBlank { null },
                richTextContent = lesson.richTextContent.ifBlank { null },
                summary = lesson.summary.ifBlank { null },
                isPremium = lesson.isPremium,
                order = index,
                quiz = buildQuiz(lesson)
            )
        }
        return CreateCourseRequest(
            title = state.title.trim(),
            description = state.description.ifBlank { null },
            subject = state.subject,
            grade = state.grade,
            lessons = lessons
        )
    }

    private fun buildQuiz(lesson: LessonForm): CreateQuizRequest? {
        if (lesson.questions.isEmpty()) return null
        return CreateQuizRequest(questions = lesson.questions.map { buildQuestion(it) })
    }

    private fun buildQuestion(form: QuestionForm): CreateQuestionRequest {
        val options = form.optionsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val steps = form.explanationText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        return CreateQuestionRequest(
            type = form.type,
            prompt = form.prompt.trim(),
            options = if (form.type == QuestionType.MULTIPLE_CHOICE && options.isNotEmpty()) options else null,
            correctAnswer = form.correctAnswer.trim(),
            explanation = if (steps.isNotEmpty()) CreateExplanationRequest(steps) else null
        )
    }
}
