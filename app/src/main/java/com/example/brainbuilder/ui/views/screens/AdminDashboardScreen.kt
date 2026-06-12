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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
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
import com.example.brainbuilder.ui.views.components.AppearOnce
import com.example.brainbuilder.ui.views.components.CircleBadge
import com.example.brainbuilder.ui.views.components.LoadingIndicator
import com.example.brainbuilder.ui.views.components.LogoutAction
import com.example.brainbuilder.ui.views.components.Pill
import com.example.brainbuilder.ui.viewmodels.AdminViewModel
import com.example.brainbuilder.util.subjectEmoji

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
    // One list at a time keeps the admin usable on a small phone screen
    var showPending by remember { mutableStateOf(true) }

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
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = showPending,
                            onClick = { showPending = true },
                            label = { Text("Pending (${uiState.pendingCourses.size})") }
                        )
                        FilterChip(
                            selected = !showPending,
                            onClick = { showPending = false },
                            label = { Text("Users (${uiState.users.size})") }
                        )
                    }

                    if (uiState.hasError) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    if (uiState.actionSuccessMessage.isNotEmpty()) {
                        Text(
                            text = uiState.actionSuccessMessage,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (showPending) {
                            if (uiState.pendingCourses.isEmpty()) {
                                item { Hint("No courses awaiting approval.") }
                            } else {
                                itemsIndexed(uiState.pendingCourses) { index, course ->
                                    AppearOnce(index = index) {
                                        PendingCourseCard(
                                            course = course,
                                            onApprove = { viewModel.approveCourse(course.id) },
                                            onReject = { rejectingCourse = course }
                                        )
                                    }
                                }
                            }
                        } else {
                            if (uiState.users.isEmpty()) {
                                item { Hint("No users found.") }
                            } else {
                                itemsIndexed(uiState.users) { index, user ->
                                    AppearOnce(index = index) {
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
                CircleBadge(size = 44.dp) {
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
                CircleBadge(
                    size = 44.dp,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                Pill(text = user.role)
                if (user.status.equals("ACTIVE", ignoreCase = true)) {
                    Pill(
                        text = user.status,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    Pill(text = user.status)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val compact = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                Button(
                    onClick = onActivate,
                    modifier = Modifier.weight(1f),
                    contentPadding = compact
                ) { Text("Activate", maxLines = 1) }
                FilledTonalButton(
                    onClick = onSuspend,
                    modifier = Modifier.weight(1f),
                    contentPadding = compact
                ) { Text("Suspend", maxLines = 1) }
                FilledTonalButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f),
                    contentPadding = compact,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) { Text("Remove", maxLines = 1) }
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
