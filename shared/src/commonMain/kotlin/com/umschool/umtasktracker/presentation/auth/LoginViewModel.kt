package com.umschool.umtasktracker.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.usecase.LoginUseCase
import com.umschool.umtasktracker.notifications.FcmTokenRegistrar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val fcmTokenRegistrar: FcmTokenRegistrar
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            loginUseCase(email, password)
                .onSuccess { result ->
                    if (result.isApproved) {
                        fcmTokenRegistrar.registerAfterLogin()
                    }
                    _uiState.value = LoginUiState.Success(
                        role = result.role,
                        isApproved = result.isApproved
                    )
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }
}
