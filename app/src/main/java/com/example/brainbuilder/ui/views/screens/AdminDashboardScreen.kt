package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.PendingCourseDto
import com.example.brainbuilder.data.remote.dto.UserItemDto
import com.example.brainbuilder.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    // Course pending rejection — holds the target while the admin types a reason
    var rejectingCourse by remember { mutableStateOf<PendingCourseDto?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Content Moderation") }) }
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

            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.hasError) {
                        item { Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error) }
                    }
                    if (uiState.actionSuccessMessage.isNotEmpty()) {
                        item { Text(uiState.actionSuccessMessage, color = MaterialTheme.colorScheme.primary) }
                    }

                    item {
                        Text("Pending Courses", style = MaterialTheme.typography.titleLarge)
                    }
                    if (uiState.pendingCourses.isEmpty()) {
                        item { Text("No courses awaiting approval.") }
                    } else {
                        items(uiState.pendingCourses) { course ->
                            PendingCourseCard(
                                course = course,
                                onApprove = { viewModel.approveCourse(course.id) },
                                onReject = { rejectingCourse = course }
                            )
                        }
                    }

                    item {
                        Text("Users", style = MaterialTheme.typography.titleLarge)
                    }
                    items(uiState.users) { user ->
                        UserCard(
                            user = user,
                            onActivate = { viewModel.activateUser(user.id) },
                            onSuspend = { viewModel.suspendUser(user.id) },
                            onRemove = { viewModel.removeUser(user.id) }
                        )
                    }
                }
            }
        }
    }

    rejectingCourse?.let { course ->
        RejectCourseDialog(
            courseTitle = course.title,
            onDismiss = { rejectingCourse = null },
            onConfirm = { reason ->
                viewModel.rejectCourse(course.id, reason)
                rejectingCourse = null
            }
        )
    }
}

@Composable
private fun PendingCourseCard(
    course: PendingCourseDto,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${course.subject} · Grade ${course.grade}")
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onApprove) { Text("Approve") }
                OutlinedButton(onClick = onReject) { Text("Reject") }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserItemDto,
    onActivate: () -> Unit,
    onSuspend: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "${user.email} · ${user.role} · ${user.status}")
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onActivate) { Text("Activate") }
                OutlinedButton(onClick = onSuspend) { Text("Suspend") }
                OutlinedButton(onClick = onRemove) { Text("Remove") }
            }
        }
    }
}

@Composable
private fun RejectCourseDialog(
    courseTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reject \"$courseTitle\"") },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Rejection reason") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            // Backend rejects REJECT_COURSE without a reason, so block empty input
            TextButton(
                onClick = { onConfirm(reason) },
                enabled = reason.isNotBlank()
            ) {
                Text("Reject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
