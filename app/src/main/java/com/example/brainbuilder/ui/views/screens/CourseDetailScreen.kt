package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.CourseDetailResponse
import com.example.brainbuilder.data.remote.dto.CourseLesson
import com.example.brainbuilder.ui.views.components.AppearOnce
import com.example.brainbuilder.ui.views.components.BackTopBar
import com.example.brainbuilder.ui.views.components.ErrorState
import com.example.brainbuilder.ui.views.components.LoadingIndicator
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
            BackTopBar(title = "Course Detail", onBack = onBack)
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
        isLoading -> LoadingIndicator()
        errorMessage != null -> ErrorState(errorMessage)
        course != null -> CourseContent(course, onLessonSelected)
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
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${course.subject} · Grade ${course.grade}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (!course.description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Lessons", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(course.lessons) { index, lesson ->
                AppearOnce(index = index) {
                    LessonItemCard(lesson = lesson) {
                        onLessonSelected(lesson.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonItemCard(
    lesson: CourseLesson,
    onClick: () -> Unit
) {
    val isLocked = lesson.isPremium && lesson.videoUrl == null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lesson.order}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // Backend blanks the summary for locked premium lessons, so this
                // never leaks gated content (NFR security)
                if (!lesson.summary.isNullOrEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = lesson.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.KeyboardArrowRight,
                contentDescription = if (isLocked) "Premium locked" else null,
                tint = if (isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}