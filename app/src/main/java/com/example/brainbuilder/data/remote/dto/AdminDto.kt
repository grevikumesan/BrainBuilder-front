package com.example.brainbuilder.data.remote.dto

data class ManageUserRequest(
    val targetId: String,
    val actionType: String,
    val reason: String? = null
)

data class ManageUserResponse(
    val success: Boolean,
    val message: String
)

data class UserItemDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String
)