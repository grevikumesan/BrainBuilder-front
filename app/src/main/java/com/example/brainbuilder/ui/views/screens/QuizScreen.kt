package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.QuestionItem
import com.example.brainbuilder.ui.uistate.QuizUiState
import com.example.brainbuilder.ui.views.components.ErrorState
import com.example.brainbuilder.ui.views.components.LoadingIndicator
import com.example.brainbuilder.ui.viewmodels.QuizViewModel
import com.example.brainbuilder.ui.views.components.BackTopBar

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
            BackTopBar(title = "Quiz", onBack = onBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.errorMessage != null -> ErrorState(uiState.errorMessage ?: "An error occurred")
                uiState.isShowingExplanations -> ExplanationView(
                    uiState = uiState,
                    onNext = { viewModel.showNextExplanation() },
                    onPrevious = { viewModel.showPreviousExplanation() },
                    onClose = { viewModel.closeExplanations() }
                )
                uiState.isSubmitted -> GradingResultView(
                    uiState = uiState,
                    onShowExplanations = { viewModel.loadExplanations() },
                    isLoadingExplanations = uiState.isLoadingExplanations
                )
                uiState.questions.isNotEmpty() -> QuizFormView(
                    uiState = uiState,
                    onAnswerChanged = { questionId, answer -> viewModel.setAnswer(questionId, answer) },
                    onSubmit = { viewModel.gradeQuiz() }
                )
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
    val answered = uiState.answers.size
    val total = uiState.questions.size
    val canSubmit = answered == total && total > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "$answered of $total answered",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { if (total == 0) 0f else answered / total.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }

        uiState.questions.forEachIndexed { index, question ->
            QuestionCard(
                index = index + 1,
                question = question,
                selectedAnswer = uiState.answers[question.id],
                onAnswerChanged = { answer -> onAnswerChanged(question.id, answer) }
            )
        }

        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Submit Quiz", style = MaterialTheme.typography.labelLarge)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(14.dp))

            when (question.type) {
                "MULTIPLE_CHOICE" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        question.options?.forEach { option ->
                            SelectableOption(
                                text = option,
                                selected = selectedAnswer == option,
                                onClick = { onAnswerChanged(option) }
                            )
                        }
                    }
                }

                "TRUE_FALSE" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("True", "False").forEach { option ->
                            SelectableOption(
                                text = option,
                                selected = selectedAnswer == option,
                                onClick = { onAnswerChanged(option) }
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
private fun SelectableOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GradingResultView(
    uiState: QuizUiState,
    onShowExplanations: () -> Unit,
    isLoadingExplanations: Boolean
) {
    val total = uiState.perQuestionCorrectness.size
    val correct = uiState.perQuestionCorrectness.count { it.isCorrect }
    val score = uiState.score?.toInt() ?: 0
    val passed = score >= 60

    val badgeColor = if (passed) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
    val onBadgeColor = if (passed) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (!uiState.anyIncorrect) "Perfect! 🎉" else "Quiz complete",
            style = MaterialTheme.typography.headlineSmall
        )

        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineMedium,
                    color = onBadgeColor
                )
                Text(
                    text = "/ 100",
                    style = MaterialTheme.typography.labelMedium,
                    color = onBadgeColor
                )
            }
        }

        Text(
            text = "$correct of $total correct",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        uiState.perQuestionCorrectness.forEachIndexed { index, result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (result.isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (result.isCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    Text(text = "Question ${index + 1}", modifier = Modifier.weight(1f))
                    Text(
                        text = if (result.isCorrect) "Correct" else "Incorrect",
                        color = if (result.isCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        if (uiState.anyIncorrect) {
            Button(
                onClick = onShowExplanations,
                enabled = !isLoadingExplanations,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoadingExplanations) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Show Explanations", style = MaterialTheme.typography.labelLarge)
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
        Text(text = "Step-by-step", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Explanation ${currentIndex + 1} of ${explanations.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (current != null && current.steps.isNotEmpty()) {
            current.steps.forEachIndexed { index, step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explanation not available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onPrevious, enabled = currentIndex > 0) {
                Text("Previous")
            }
            OutlinedButton(onClick = onClose) {
                Text("Close")
            }
            Button(onClick = onNext, enabled = currentIndex < explanations.size - 1) {
                Text("Next")
            }
        }
    }
}
