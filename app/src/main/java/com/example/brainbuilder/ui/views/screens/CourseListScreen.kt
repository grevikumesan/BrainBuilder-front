package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.CourseItem
import com.example.brainbuilder.ui.views.components.EmptyState
import com.example.brainbuilder.ui.views.components.ErrorState
import com.example.brainbuilder.ui.views.components.LoadingIndicator
import com.example.brainbuilder.ui.viewmodels.CourseViewModel

private val SUBJECTS = listOf("MATHEMATICS", "PHYSICS", "CHEMISTRY")
private val GRADES = listOf("X", "XI", "XII")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel,
    onCourseSelected: (courseId: String) -> Unit,
    onOpenProgress: () -> Unit
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
                        items(uiState.courses) { course ->
                            CourseCard(course = course, onClick = { onCourseSelected(course.id) })
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
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.title)
            Text(text = "${course.subject} · Grade ${course.grade}")
            if (!course.description.isNullOrEmpty()) {
                Text(text = course.description)
            }
        }
    }
}