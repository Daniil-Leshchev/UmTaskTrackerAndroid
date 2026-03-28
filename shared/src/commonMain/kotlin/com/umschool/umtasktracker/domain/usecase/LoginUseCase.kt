package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.domain.repository.AuthRepository

// Бизнес-логика входа: логин → получение профиля → определение роли
// Возвращает UserRole для навигационного роутинга
class LoginUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<UserRole> {
        // Получаем токены
        val tokenResult = repository.login(email, password)
        val token = tokenResult.getOrElse { return Result.failure(it) }

        // Получаем профиль для определения роли
        val profileResult = repository.getProfile(token.access)
        val profile = profileResult.getOrElse { return Result.failure(it) }

        return Result.success(UserRole.fromProfile(profile))
    }
}
