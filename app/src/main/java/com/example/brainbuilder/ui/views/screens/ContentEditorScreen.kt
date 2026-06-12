package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.brainbuilder.ui.views.components.LogoutAction
import com.example.brainbuilder.ui.views.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEditorScreen(
    viewModel: ContentEditorViewModel,
    onLogout: () -> Unit
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
        topBar = {
            TopAppBar(
                title = { Text("Create Course") },
                actions = { LogoutAction(onLogout = onLogout) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Sticky submit so the teacher never has to scroll past every lesson to save
            if (uiState.createdCourseId == null) {
                Surface(shadowElevation = 8.dp) {
                    PrimaryButton(
                        text = "Create Course",
                        onClick = { viewModel.submit() },
                        loading = uiState.isSubmitting,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
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
                        singleLine = true,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lesson ${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
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
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lesson.videoUrl,
                onValueChange = { value -> viewModel.updateLesson(index) { it.copy(videoUrl = value) } },
                label = { Text("Video URL (optional)") },
                singleLine = true,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
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
            when (question.type) {
                QuestionType.MULTIPLE_CHOICE -> {
                    OutlinedTextField(
                        value = question.optionsText,
                        onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(optionsText = value) } },
                        label = { Text("Options (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Pick the correct answer from the options the teacher typed, so it can
                    // never mismatch what the student selects.
                    val options = question.optionsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (options.isEmpty()) {
                        Text(
                            text = "Add options above, then pick the correct one.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        OptionSelector(
                            label = "Correct answer",
                            options = options,
                            selected = question.correctAnswer,
                            optionLabel = { it },
                            onSelect = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(correctAnswer = value) } }
                        )
                    }
                }
                // True/False: pick the correct answer so it always matches what the
                // student selects ("True"/"False") — no fragile free-text typing.
                QuestionType.TRUE_FALSE -> {
                    OptionSelector(
                        label = "Correct answer",
                        options = listOf("True", "False"),
                        selected = question.correctAnswer,
                        optionLabel = { it },
                        onSelect = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(correctAnswer = value) } }
                    )
                }
                QuestionType.SHORT_ANSWER -> {
                    OutlinedTextField(
                        value = question.correctAnswer,
                        onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(correctAnswer = value) } },
                        label = { Text("Correct answer") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            OutlinedTextField(
                value = question.explanationText,
                onValueChange = { value -> viewModel.updateQuestion(lessonIndex, questionIndex) { it.copy(explanationText = value) } },
                label = { Text("Explanation steps (one per line, optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(optionLabel(option)) }
                )
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
        Text("✅", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Course submitted!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Status: Pending Approval",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Course ID: $courseId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateAnother,
            modifier = Modifier.height(50.dp)
        ) {
            Text("Create Another Course", style = MaterialTheme.typography.labelLarge)
        }
    }
}
