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

        fun from(roleId: Int): UserRole = when (roleId) {
            1, 2, 3 -> Curator
            7, 9    -> Admin
            else    -> Manager
        }
    }
}
