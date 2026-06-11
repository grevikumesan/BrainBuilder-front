package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainbuilder.data.remote.dto.PendingCourseDto
import com.example.brainbuilder.data.remote.dto.UserItemDto
import com.example.brainbuilder.ui.views.components.LoadingIndicator
import com.example.brainbuilder.ui.views.components.LogoutAction
import com.example.brainbuilder.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    // Course pending rejection — holds the target while the admin types a reason
    var rejectingCourse by remember { mutableStateOf<PendingCourseDto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderation") },
                actions = { LogoutAction(onLogout = onLogout) }
            )
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.hasError) {
                        item {
                            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    if (uiState.actionSuccessMessage.isNotEmpty()) {
                        item {
                            Text(uiState.actionSuccessMessage, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    item { SectionHeader("Pending courses", uiState.pendingCourses.size) }
                    if (uiState.pendingCourses.isEmpty()) {
                        item { Hint("No courses awaiting approval.") }
                    } else {
                        items(uiState.pendingCourses) { course ->
                            PendingCourseCard(
                                course = course,
                                onApprove = { viewModel.approveCourse(course.id) },
                                onReject = { rejectingCourse = course }
                            )
                        }
                    }

                    item { SectionHeader("Users", uiState.users.size) }
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
private fun SectionHeader(title: String, count: Int) {
    Text(
        text = "$title ($count)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun Hint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PendingCourseCard(
    course: PendingCourseDto,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = subjectEmoji(course.subject), fontSize = 22.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${course.subject} · Grade ${course.grade}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = user.role, active = false)
                StatusBadge(text = user.status, active = user.status.equals("ACTIVE", ignoreCase = true))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onActivate) { Text("Activate") }
                OutlinedButton(onClick = onSuspend) { Text("Suspend") }
                OutlinedButton(onClick = onRemove) { Text("Remove") }
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, active: Boolean) {
    val container = if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val content = if (active) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = content)
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

private fun subjectEmoji(subject: String): String = when (subject.uppercase()) {
    "MATHEMATICS" -> "🧮"
    "PHYSICS" -> "🔬"
    "CHEMISTRY" -> "🧪"
    else -> "📘"
}
