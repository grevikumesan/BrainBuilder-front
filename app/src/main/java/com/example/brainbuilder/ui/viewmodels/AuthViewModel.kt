package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.dto.LoginRequest
import com.example.brainbuilder.data.remote.dto.RegisterRequest
import com.example.brainbuilder.data.remote.repository.AuthRepository
import com.example.brainbuilder.ui.uistate.LoginUiState
import com.example.brainbuilder.ui.uistate.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val dataStore: DataStore
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun updateLoginEmail(email: String) {
        _loginUiState.update { it.copy(emailInput = email) }
    }

    fun updateLoginPassword(password: String) {
        _loginUiState.update { it.copy(passwordInput = password) }
    }

    fun updateRegisterName(name: String) {
        _registerUiState.update { it.copy(nameInput = name) }
    }

    fun updateRegisterEmail(email: String) {
        _registerUiState.update { it.copy(emailInput = email) }
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.update { it.copy(passwordInput = password) }
    }

    fun updateRegisterRole(role: String) {
        _registerUiState.update { it.copy(roleInput = role) }
    }

    fun login() {
        _loginUiState.update { it.copy(isLoading = true, hasError = false) }
        viewModelScope.launch {
            try {
                val request = LoginRequest(_loginUiState.value.emailInput, _loginUiState.value.passwordInput)
                val response = repository.loginUser(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data
                    dataStore.saveToken(authData.accessToken)
                    _loginUiState.update { it.copy(isLoading = false, isSuccess = true, userRole = authData.role) }
                } else {
                    _loginUiState.update { it.copy(isLoading = false, hasError = true, errorMessage = "Invalid email or password") }
                }
            } catch (e: Exception) {
                _loginUiState.update { it.copy(isLoading = false, hasError = true, errorMessage = e.message ?: "Network error occurred") }
            }
        }
    }

    fun register() {
        _registerUiState.update { it.copy(isLoading = true, hasError = false) }
        viewModelScope.launch {
            try {
                val state = _registerUiState.value
                val request = RegisterRequest(state.nameInput, state.emailInput, state.passwordInput, state.roleInput)
                val response = repository.registerUser(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _registerUiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _registerUiState.update { it.copy(isLoading = false, hasError = true, errorMessage = "Registration failed") }
                }
            } catch (e: Exception) {
                _registerUiState.update { it.copy(isLoading = false, hasError = true, errorMessage = e.message ?: "Network error occurred") }
            }
        }
    }
}