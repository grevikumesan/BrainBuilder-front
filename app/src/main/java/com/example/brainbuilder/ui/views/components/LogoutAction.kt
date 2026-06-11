package com.example.brainbuilder.ui.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

/** Top-bar logout button for the role-home screens (CourseList, ContentEditor, Admin). */
@Composable
fun LogoutAction(onLogout: () -> Unit) {
    IconButton(onClick = onLogout) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
    }
}
