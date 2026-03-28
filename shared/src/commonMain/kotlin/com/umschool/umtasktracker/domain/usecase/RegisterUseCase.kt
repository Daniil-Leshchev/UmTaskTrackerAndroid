package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.repository.CatalogRepository

// Бизнес-логика регистрации: валидация полей + отправка на бэкенд
class RegisterUseCase(private val repository: CatalogRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ): Result<Unit> {
        // Валидация на клиенте
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Введите email"))
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Некорректный email"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Пароль должен быть не менее 6 символов"))
        if (firstName.isBlank()) return Result.failure(IllegalArgumentException("Введите имя"))
        if (lastName.isBlank()) return Result.failure(IllegalArgumentException("Введите фамилию"))

        return repository.register(
            email = email.trim(),
            password = password,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            roleId = roleId,
            subjectId = subjectId,
            departmentId = departmentId
        )
    }
}
