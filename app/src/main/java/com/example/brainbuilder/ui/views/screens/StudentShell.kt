package com.example.brainbuilder.ui.views.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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

private enum class StudentTab { HOME, COURSES, PREMIUM, YOU }

/**
 * Student "home" shell: a 4-tab bottom navigation (Home / Courses / Premium / You).
 * Drill-down screens (course detail, lesson, quiz, payment, progress) are pushed over
 * this shell by the host NavHost.
 */
@Composable
fun StudentShell(
    courseViewModel: CourseViewModel,
    paymentViewModel: PaymentViewModel,
    onCourseSelected: (String) -> Unit,
    onPayNow: (String) -> Unit,
    onOpenProgress: () -> Unit,
    onLogout: () -> Unit
) {
    var tab by remember { mutableStateOf(StudentTab.HOME) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (tab) {
                StudentTab.HOME -> HomeScreen(onBrowse = { tab = StudentTab.COURSES })
                StudentTab.COURSES -> CourseListScreen(
                    viewModel = courseViewModel,
                    onCourseSelected = onCourseSelected
                )
                StudentTab.PREMIUM -> SubscriptionScreen(
                    viewModel = paymentViewModel,
                    onPayNow = onPayNow,
                    onBack = { tab = StudentTab.HOME }
                )
                StudentTab.YOU -> ProfileScreen(
                    onOpenProgress = onOpenProgress,
                    onLogout = onLogout
                )
            }
        }
        NavigationBar {
            NavigationBarItem(
                selected = tab == StudentTab.HOME,
                onClick = { tab = StudentTab.HOME },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = tab == StudentTab.COURSES,
                onClick = { tab = StudentTab.COURSES },
                icon = { Icon(Icons.Default.School, contentDescription = null) },
                label = { Text("Courses") }
            )
            NavigationBarItem(
                selected = tab == StudentTab.PREMIUM,
                onClick = { tab = StudentTab.PREMIUM },
                icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null) },
                label = { Text("Premium") }
            )
            NavigationBarItem(
                selected = tab == StudentTab.YOU,
                onClick = { tab = StudentTab.YOU },
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("You") }
            )
        }
    }
}
