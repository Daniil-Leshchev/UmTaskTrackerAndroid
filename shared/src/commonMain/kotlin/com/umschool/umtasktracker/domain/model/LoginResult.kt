package com.umschool.umtasktracker.domain.model

data class LoginResult(
    val role: UserRole,
    val isApproved: Boolean
)