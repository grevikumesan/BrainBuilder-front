package com.example.brainbuilder.ui.uistate

import com.example.brainbuilder.data.remote.dto.UserItemDto

data class AdminUiState(
    val users: List<UserItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val actionSuccessMessage: String = ""
)