package com.umschool.umtasktracker.presentation.auth

import com.umschool.umtasktracker.domain.model.UserRole

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val role: UserRole) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
