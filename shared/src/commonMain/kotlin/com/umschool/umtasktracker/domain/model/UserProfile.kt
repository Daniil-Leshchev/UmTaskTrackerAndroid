package com.umschool.umtasktracker.domain.model

// Соответствует GET /api/users/me/ — профиль текущего пользователя
data class UserProfile(
    val email: String,
    val name: String,
    val isAdmin: Boolean,
    val roleName: String
)
