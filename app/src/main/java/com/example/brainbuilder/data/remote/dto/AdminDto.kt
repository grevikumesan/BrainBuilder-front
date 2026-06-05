package com.example.brainbuilder.data.remote.dto

data class ManageActionRequest(
    val targetId: String,
    val action: String,
    val reason: String? = null
)

data class ActionResultData(
    val message: String
)

data class AdminItemsData(
    val users: List<UserItemDto> = emptyList(),
    val pendingCourses: List<PendingCourseDto> = emptyList()
)

data class UserItemDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val createdAt: String
)

data class PendingCourseDto(
    val id: String,
    val title: String,
    val subject: String,
    val grade: String,
    val teacherId: String,
    val createdAt: String
)
