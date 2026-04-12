package com.umschool.umtasktracker.domain.model
data class UserProfile(
    val email: String,
    val name: String,
    val isAdmin: Boolean,
    val roleName: String,
    val isApproved: Boolean
)
