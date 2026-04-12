package com.umschool.umtasktracker.domain.model

sealed class UserRole {
    data object Curator : UserRole()
    data object Manager : UserRole()
    data object Admin : UserRole()

    companion object {
        fun fromProfile(profile: UserProfile): UserRole = when {
            profile.isAdmin -> Admin
            profile.roleName.lowercase().contains("куратор") -> Curator
            else -> Manager
        }
    }
}
