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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainbuilder.data.remote.dto.CourseItem
import com.example.brainbuilder.ui.views.components.AppearOnce
import com.example.brainbuilder.ui.views.components.CircleBadge
import com.example.brainbuilder.ui.views.components.EmptyState
import com.example.brainbuilder.ui.views.components.LogoutAction
import com.example.brainbuilder.ui.views.components.ErrorState
import com.example.brainbuilder.ui.views.components.LoadingIndicator
import com.example.brainbuilder.ui.viewmodels.CourseViewModel
import com.example.brainbuilder.util.subjectEmoji

private val SUBJECTS = listOf("MATHEMATICS", "PHYSICS", "CHEMISTRY")
private val GRADES = listOf("X", "XI", "XII")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel,
    onCourseSelected: (courseId: String) -> Unit,
    onOpenProgress: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var isSubjectExpanded by remember { mutableStateOf(false) }
    var isGradeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.selectedSubject, uiState.selectedGrade) {
        viewModel.fetchCourses(uiState.selectedSubject, uiState.selectedGrade)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                actions = {
                    TextButton(onClick = onOpenProgress) { Text("Progress") }
                    LogoutAction(onLogout = onLogout)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = isSubjectExpanded,
                    onExpandedChange = { isSubjectExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isSubjectExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isSubjectExpanded,
                        onDismissRequest = { isSubjectExpanded = false }
                    ) {
                        SUBJECTS.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = {
                                    viewModel.fetchCourses(subject, uiState.selectedGrade)
                                    isSubjectExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = isGradeExpanded,
                    onExpandedChange = { isGradeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedGrade,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grade") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isGradeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isGradeExpanded,
                        onDismissRequest = { isGradeExpanded = false }
                    ) {
                        GRADES.forEach { grade ->
                            DropdownMenuItem(
                                text = { Text(grade) },
                                onClick = {
                                    viewModel.fetchCourses(uiState.selectedSubject, grade)
                                    isGradeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.errorMessage != null -> ErrorState(uiState.errorMessage ?: "An error occurred")
                uiState.courses.isEmpty() -> EmptyState("No courses available for this filter.")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(uiState.courses) { index, course ->
                            AppearOnce(index = index) {
                                CourseCard(course = course, onClick = { onCourseSelected(course.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(course: CourseItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircleBadge(size = 52.dp) {
                Text(text = subjectEmoji(course.subject), fontSize = 26.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${course.subject} · Grade ${course.grade}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!course.description.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
