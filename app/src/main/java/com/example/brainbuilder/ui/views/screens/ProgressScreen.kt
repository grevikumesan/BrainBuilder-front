package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.CourseProgress
import com.example.brainbuilder.data.remote.dto.QuizScoreHistory
import com.example.brainbuilder.data.remote.dto.RecommendedTopic
import com.example.brainbuilder.ui.viewmodels.ProgressViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProgress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Empty state for a student who has not opened any lesson yet (UC-05 extension 2a).
            !uiState.hasStarted -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start your first lesson to see your progress here.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { SectionTitle("Course Completion") }
                    if (uiState.courses.isEmpty()) {
                        item { EmptyHint("No courses started yet.") }
                    } else {
                        items(uiState.courses) { course -> CourseProgressCard(course) }
                    }

                    item { SectionTitle("Quiz Score History") }
                    if (uiState.scoreHistory.isEmpty()) {
                        item { EmptyHint("No quiz attempts yet.") }
                    } else {
                        items(uiState.scoreHistory) { entry -> ScoreHistoryCard(entry) }
                    }

                    item { SectionTitle("Recommended to Review") }
                    if (uiState.recommendedTopics.isEmpty()) {
                        item { EmptyHint("Nothing to review — great job!") }
                    } else {
                        items(uiState.recommendedTopics) { topic -> RecommendedTopicCard(topic) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun CourseProgressCard(course: CourseProgress) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = course.courseTitle, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "${course.subject} · Grade ${course.grade}",
                style = MaterialTheme.typography.bodySmall
            )
            LinearProgressIndicator(
                progress = { (course.completionPct / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${course.completionPct.roundToInt()}% · ${course.completedLessons}/${course.totalLessons} lessons",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ScoreHistoryCard(entry: QuizScoreHistory) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = entry.lessonTitle, style = MaterialTheme.typography.titleSmall)
            Text(text = entry.courseTitle, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Score: ${entry.score.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RecommendedTopicCard(topic: RecommendedTopic) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = topic.lessonTitle, style = MaterialTheme.typography.titleSmall)
            Text(text = topic.courseTitle, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Avg score: ${topic.avgScore.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
