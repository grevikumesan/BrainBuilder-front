package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.Grade
import com.example.brainbuilder.data.remote.dto.QuestionType
import com.example.brainbuilder.data.remote.dto.Subject
import com.example.brainbuilder.ui.uistate.LessonForm
import com.example.brainbuilder.ui.uistate.QuestionForm
import com.example.brainbuilder.ui.viewmodels.ContentEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEditorScreen(
    viewModel: ContentEditorViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Course") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val createdId = uiState.createdCourseId
        if (createdId != null) {
            CourseCreatedMessage(
                courseId = createdId,
                onCreateAnother = { viewModel.startNewCourse() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("Course Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::updateDescription,
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OptionSelector(
                        label = "Subject",
                        options = Subject.values().toList(),
                        selected = uiState.subject,
                        optionLabel = { it.name },
                        onSelect = viewModel::selectSubject
                    )
                }
                item {
                    OptionSelector(
                        label = "Grade",
                        options = Grade.values().toList(),
                        selected = uiState.grade,
                        optionLabel = { it.name },
                        onSelect = viewModel::selectGrade
                    )
                }
                item {
                    Text("Lessons", style = MaterialTheme.typography.titleMedium)
                }
                itemsIndexed(uiState.lessons) { index, lesson ->
                    LessonEditor(
                        index = index,
                        lesson = lesson,
                        canRemove = uiState.lessons.size > 1,
                        viewModel = viewModel
                    )
                }
                item {
                    OutlinedButton(
                        onClick = { viewModel.addLesson() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Add Lesson")
                    }
                }
                item {
                    Button(
                        onClick = { viewModel.submit() },
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Create Course")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonEditor(
    index: Int,
    lesson: LessonForm,
    canRemove: Boolean,
    viewModel: ContentEditorViewModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lesson ${index + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                if (canRemove) {
                    TextButton(onClick = { viewModel.removeLesson(index) }) { Text("Remove") }
                }
            }
            OutlinedTextField(
                value = lesson.title,
                onValueChange = { value -> viewModel.updateLesson(index) { it.copy(title = value) } },
                label = { Text("Lesson Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lesson.videoUrl,
                onValueChange = { value -> viewModel.updateLesson(index) { it.copy(videoUrl = value) } },
                label = { Text("Video URL (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lesson.richTextContent,
                onValueChange = { value -> viewModel.updateLesson(index) { it.copy(richTextContent = value) } },
                label = { Text("Material (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lesson.summary,
                onValueChange = { value -> viewModel.updateLesson(index) { it.copy(summary = value) } },
                label = { Text("Summary (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = lesson.isPremium,
                    onCheckedChange = { value -> viewModel.updateLesson(index) { it.copy(isPremium = value) } }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Premium lesson")
            }
            Text("Questions", style = MaterialTheme.typography.labelLarge)
            lesson.questions.forEachIndexed { questionIndex, question ->
                QuestionEditor(
                    lessonIndex = index,
                    questionIndex = questionIndex,
                    question = question,
                    viewModel = viewModel
                )
            }
            OutlinedButton(
                onClick = { viewModel.addQuestion(index) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Question")
            }
        }
    }
}

@Composable
private fun QuestionEditor(
    lessonIndex: Int,
    questionIndex: Int,
    question: QuestionForm,
    viewModel: ContentEditorViewModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Question ${questionIndex + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { viewModel.removeQuestion(lessonIndex, questionIndex) }) {
                    Text("Remove")
                }
            }
            OptionSelector(
                label = "Type",
                options = QuestionType.values().toList(),
                selected = question.type,
                optionLabel = { it.name },
                onSelect = { type -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(type = type) } }
            )
            OutlinedTextField(
                value = question.prompt,
                onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(prompt = value) } },
                label = { Text("Prompt") },
                modifier = Modifier.fillMaxWidth()
            )
            if (question.type == QuestionType.MULTIPLE_CHOICE) {
                OutlinedTextField(
                    value = question.optionsText,
                    onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(optionsText = value) } },
                    label = { Text("Options (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = question.correctAnswer,
                onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(correctAnswer = value) } },
                label = { Text("Correct Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = question.explanationText,
                onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(explanationText = value) } },
                label = { Text("Explanation steps (one per line, optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun <T> OptionSelector(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = option == selected,
                    onClick = { onSelect(option) }
                )
                Text(text = optionLabel(option))
            }
        }
    }
}

@Composable
private fun CourseCreatedMessage(
    courseId: String,
    onCreateAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Course submitted!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Status: PENDING_APPROVAL", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Course ID: $courseId", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateAnother) { Text("Create Another Course") }
    }
}
