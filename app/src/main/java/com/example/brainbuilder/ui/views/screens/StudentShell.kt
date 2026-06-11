package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.brainbuilder.ui.viewmodels.CourseViewModel
import com.example.brainbuilder.ui.viewmodels.PaymentViewModel
import com.example.brainbuilder.ui.viewmodels.ProgressViewModel

private enum class StudentTab { COURSES, PROGRESS, PREMIUM }

/**
 * The student "home" shell: a bottom navigation bar over the Courses / Progress /
 * Premium tabs. Drill-down screens (course detail, lesson, quiz, payment) are pushed
 * over this shell by the host NavHost.
 */
@Composable
fun StudentShell(
    courseViewModel: CourseViewModel,
    progressViewModel: ProgressViewModel,
    paymentViewModel: PaymentViewModel,
    onCourseSelected: (String) -> Unit,
    onPayNow: (String) -> Unit,
    onLogout: () -> Unit
) {
    var tab by remember { mutableStateOf(StudentTab.COURSES) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (tab) {
                StudentTab.COURSES -> CourseListScreen(
                    viewModel = courseViewModel,
                    onCourseSelected = onCourseSelected,
                    onOpenProgress = { tab = StudentTab.PROGRESS },
                    onLogout = onLogout
                )
                StudentTab.PROGRESS -> ProgressScreen(
                    viewModel = progressViewModel,
                    onBack = { tab = StudentTab.COURSES }
                )
                StudentTab.PREMIUM -> SubscriptionScreen(
                    viewModel = paymentViewModel,
                    onPayNow = onPayNow,
                    onBack = { tab = StudentTab.COURSES }
                )
            }
        }
        NavigationBar {
            NavigationBarItem(
                selected = tab == StudentTab.COURSES,
                onClick = { tab = StudentTab.COURSES },
                icon = { Icon(Icons.Default.School, contentDescription = null) },
                label = { Text("Courses") }
            )
            NavigationBarItem(
                selected = tab == StudentTab.PROGRESS,
                onClick = { tab = StudentTab.PROGRESS },
                icon = { Icon(Icons.Default.Insights, contentDescription = null) },
                label = { Text("Progress") }
            )
            NavigationBarItem(
                selected = tab == StudentTab.PREMIUM,
                onClick = { tab = StudentTab.PREMIUM },
                icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null) },
                label = { Text("Premium") }
            )
        }
    }
}
