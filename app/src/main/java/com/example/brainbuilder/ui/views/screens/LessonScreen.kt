package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.brainbuilder.ui.viewmodels.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    viewModel: CourseViewModel,
    onStartQuiz: (lessonId: String) -> Unit,
    onSubscribeRequired: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.fetchLesson(lessonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(uiState.selectedLesson?.title ?: "Lesson") })
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            !uiState.accessAllowed -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("This lesson requires a premium subscription.")
                        Button(onClick = onSubscribeRequired) {
                            Text("Subscribe to continue")
                        }
                    }
                }
            }
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage ?: "An error occurred")
                }
            }
            uiState.selectedLesson != null -> {
                val lesson = uiState.selectedLesson!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = lesson.title)
                    if (!lesson.videoUrl.isNullOrEmpty()) {
                        Text(text = "Video: ${lesson.videoUrl}")
                    }
                    if (!lesson.richTextContent.isNullOrEmpty()) {
                        Text(text = lesson.richTextContent)
                    }
                    if (!lesson.summary.isNullOrEmpty()) {
                        Text(text = lesson.summary)
                    }
                    if (lesson.quiz != null) {
                        Button(
                            // Pass the lesson id; the quiz screen re-fetches the lesson
                            // detail to get the quiz and its questions
                            onClick = { onStartQuiz(lessonId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Quiz")
                        }
                    }
                }
            }
        }
    }
}