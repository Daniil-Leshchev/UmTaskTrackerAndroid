package com.umschool.umtasktracker.domain.model

// Роли пользователей — соответствуют бэкенду
sealed class UserRole {
    data object Curator : UserRole()    // Куратор (исполнитель)
    data object Manager : UserRole()    // Менеджер
    data object Admin : UserRole()      // Менеджер + суперюзер

    companion object {
        // Определяем роль по данным профиля
        // is_admin=true → Admin, roleName содержит "куратор" → Curator, иначе → Manager
        fun fromProfile(profile: UserProfile): UserRole = when {
            profile.isAdmin -> Admin
            profile.roleName.lowercase().contains("куратор") -> Curator
            else -> Manager
        }

        // Запасной вариант по id_role (если бэкенд вернёт числовой id)
        fun from(roleId: Int): UserRole = when (roleId) {
            1, 2, 3 -> Curator
            7, 9    -> Admin
            else    -> Manager
        }
    }
}
