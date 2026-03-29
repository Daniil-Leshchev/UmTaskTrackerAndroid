package com.umschool.umtasktracker.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.usecase.LoadCatalogsUseCase
import com.umschool.umtasktracker.domain.usecase.LoginUseCase
import com.umschool.umtasktracker.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val loadCatalogsUseCase: LoadCatalogsUseCase,
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        loadCatalogs()
    }

    fun loadCatalogs() {
        _uiState.update { it.copy(isCatalogsLoading = true, catalogsError = null) }
        viewModelScope.launch {
            loadCatalogsUseCase()
                .onSuccess { catalogs ->
                    _uiState.update {
                        it.copy(
                            roles = catalogs.roles,
                            subjects = catalogs.subjects,
                            departments = catalogs.departments,
                            isCatalogsLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCatalogsLoading = false,
                            catalogsError = error.message ?: "Ошибка загрузки каталогов"
                        )
                    }
                }
        }
    }

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ) {
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            registerUseCase(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                roleId = roleId,
                subjectId = subjectId,
                departmentId = departmentId
            ).onSuccess {
                autoLogin(email, password)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "Ошибка регистрации"
                    )
                }
            }
        }
    }

    private suspend fun autoLogin(email: String, password: String) {
        loginUseCase(email, password)
            .onSuccess { role ->
                _uiState.update {
                    it.copy(isSubmitting = false, loginSuccess = role)
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        loginSuccess = null,
                        submitError = "Регистрация успешна! Войдите с вашими данными."
                    )
                }
            }
    }

    fun clearError() {
        _uiState.update { it.copy(submitError = null) }
    }
}
