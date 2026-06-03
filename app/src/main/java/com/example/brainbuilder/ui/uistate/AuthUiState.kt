package com.example.brainbuilder.ui.uistate

data class LoginUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false,
    val userRole: String = ""
)

data class RegisterUiState(
    val nameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val roleInput: String = "STUDENT",
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
)