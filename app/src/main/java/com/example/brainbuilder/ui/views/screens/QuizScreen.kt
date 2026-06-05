package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.QuestionItem
import com.example.brainbuilder.ui.uistate.QuizUiState
import com.example.brainbuilder.ui.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lessonId: String,
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadQuiz(lessonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                uiState.isShowingExplanations -> {
                    ExplanationView(
                        uiState = uiState,
                        onNext = { viewModel.showNextExplanation() },
                        onPrevious = { viewModel.showPreviousExplanation() },
                        onClose = { viewModel.closeExplanations() }
                    )
                }

                uiState.isSubmitted -> {
                    GradingResultView(
                        uiState = uiState,
                        onShowExplanations = { viewModel.loadExplanations() },
                        isLoadingExplanations = uiState.isLoadingExplanations
                    )
                }

                uiState.questions.isNotEmpty() -> {
                    QuizFormView(
                        uiState = uiState,
                        onAnswerChanged = { questionId, answer ->
                            viewModel.setAnswer(questionId, answer)
                        },
                        onSubmit = { viewModel.gradeQuiz() }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizFormView(
    uiState: QuizUiState,
    onAnswerChanged: (String, String) -> Unit,
    onSubmit: () -> Unit
) {
    val canSubmit = uiState.answers.size == uiState.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.questions.forEachIndexed { index, question ->
            QuestionCard(
                index = index + 1,
                question = question,
                selectedAnswer = uiState.answers[question.id],
                onAnswerChanged = { answer -> onAnswerChanged(question.id, answer) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Quiz")
        }
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    question: QuestionItem,
    selectedAnswer: String?,
    onAnswerChanged: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Question $index",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            when (question.type) {
                "MULTIPLE_CHOICE" -> {
                    question.options?.forEach { option ->
                        FilterChip(
                            selected = selectedAnswer == option,
                            onClick = { onAnswerChanged(option) },
                            label = { Text(option) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }
                }

                "TRUE_FALSE" -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("True", "False").forEach { option ->
                            FilterChip(
                                selected = selectedAnswer == option,
                                onClick = { onAnswerChanged(option) },
                                label = { Text(option) }
                            )
                        }
                    }
                }

                "SHORT_ANSWER" -> {
                    OutlinedTextField(
                        value = selectedAnswer ?: "",
                        onValueChange = onAnswerChanged,
                        label = { Text("Your answer") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun GradingResultView(
    uiState: QuizUiState,
    onShowExplanations: () -> Unit,
    isLoadingExplanations: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quiz Result",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "${uiState.score?.toInt() ?: 0} / 100",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        uiState.perQuestionCorrectness.forEachIndexed { index, result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Question ${index + 1}")
                    Text(
                        text = if (result.isCorrect) "Correct" else "Incorrect",
                        color = if (result.isCorrect) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }

        if (uiState.anyIncorrect) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onShowExplanations,
                enabled = !isLoadingExplanations,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoadingExplanations) {
                    CircularProgressIndicator()
                } else {
                    Text("Show Explanations")
                }
            }
        }
    }
}

@Composable
private fun ExplanationView(
    uiState: QuizUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit
) {
    val explanations = uiState.cachedExplanations
    val currentIndex = uiState.currentExplanationIndex
    val current = explanations.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Explanation ${currentIndex + 1} of ${explanations.size}",
            style = MaterialTheme.typography.titleMedium
        )

        if (current != null && current.steps.isNotEmpty()) {
            current.steps.forEachIndexed { index, step ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Step ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Text("Explanation not available yet.")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentIndex > 0
            ) {
                Text("Previous")
            }

            OutlinedButton(onClick = onClose) {
                Text("Close")
            }

            OutlinedButton(
                onClick = onNext,
                enabled = currentIndex < explanations.size - 1
            ) {
                Text("Next")
            }
        }
    }
}