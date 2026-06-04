package com.example.brainbuilder.ui.views.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.brainbuilder.data.remote.dto.CourseDetailResponse
import com.example.brainbuilder.data.remote.dto.LessonDetail
import com.example.brainbuilder.ui.viewmodels.CourseDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: CourseDetailViewModel,
    onBack: () -> Unit,
    onLessonSelected: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetail(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            RenderState(uiState.isLoading, uiState.errorMessage, uiState.course, onLessonSelected)
        }
    }
}

@Composable
private fun RenderState(
    isLoading: Boolean,
    errorMessage: String?,
    course: CourseDetailResponse?,
    onLessonSelected: (String) -> Unit
) {
    when {
        isLoading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        errorMessage != null -> {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        course != null -> {
            CourseContent(course, onLessonSelected)
        }
    }
}

@Composable
private fun CourseContent(
    course: CourseDetailResponse,
    onLessonSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = course.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${course.subject} - Grade ${course.grade}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(course.lessons) { lesson ->
                LessonItemCard(lesson = lesson) {
                    onLessonSelected(lesson.id)
                }
            }
        }
    }
}

@Composable
private fun LessonItemCard(
    lesson: LessonDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lesson ${lesson.order}: ${lesson.title}",
                    style = MaterialTheme.typography.titleMedium
                )
                // Obfuscated summary handling prevents data leakage (NFR Security)
                if (lesson.summary != null) {
                    Text(
                        text = lesson.summary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (lesson.isPremium && lesson.videoUrl == null) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Premium Locked",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}