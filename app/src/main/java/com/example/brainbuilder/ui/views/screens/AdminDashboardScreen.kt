package com.example.brainbuilder.ui.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.ui.viewmodels.AdminViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B1021), Color(0xFF1B2745))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        // High-contrast typography ensures readability against the dark background
        Text(
            text = "Content Moderation Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = { viewModel.suspendUser("dummy-user-id") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Suspend User")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.activateUser("dummy-user-id") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Activate User")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.rejectCourse("dummy-course-id", "Inappropriate Content") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Reject Course")
            }
        }
    }
}