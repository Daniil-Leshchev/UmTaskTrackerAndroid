package com.umschool.umtasktracker.presentation.auth

import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.model.UserRole

// Состояние экрана регистрации
data class RegisterUiState(
    // Каталоги для выпадающих списков
    val roles: List<CatalogItem> = emptyList(),
    val subjects: List<CatalogItem> = emptyList(),
    val departments: List<CatalogItem> = emptyList(),

    // Загрузка каталогов
    val isCatalogsLoading: Boolean = true,
    val catalogsError: String? = null,

    // Состояние отправки формы
    val isSubmitting: Boolean = false,
    val submitError: String? = null,

    // Успешная регистрация + автоматический вход
    val loginSuccess: UserRole? = null
)
