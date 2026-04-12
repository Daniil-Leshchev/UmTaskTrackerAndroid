package com.umschool.umtasktracker.presentation.auth

import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.model.UserRole

data class RegisterUiState(
    val roles: List<CatalogItem> = emptyList(),
    val subjects: List<CatalogItem> = emptyList(),
    val departments: List<CatalogItem> = emptyList(),

    val isCatalogsLoading: Boolean = true,
    val catalogsError: String? = null,

    val isSubmitting: Boolean = false,
    val submitError: String? = null,

    val loginSuccess: UserRole? = null,
    val isRegistrationSuccess: Boolean = false
)
