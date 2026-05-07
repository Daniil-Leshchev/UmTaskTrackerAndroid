package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.repository.ManagerRepository

class CreateTaskUseCase(
    private val repository: ManagerRepository
) {
    suspend operator fun invoke(params: CreateTaskParams): Result<CreateTaskResult> {
        if (params.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Название задачи обязательно"))
        }
        if (params.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Описание задачи обязательно"))
        }
        if (params.reportFormat.isBlank()) {
            return Result.failure(IllegalArgumentException("Форма отчёта обязательна"))
        }
        when (params) {
            is CreateTaskParams.Individual -> if (params.emails.isEmpty()) {
                return Result.failure(IllegalArgumentException("Выберите хотя бы одного исполнителя"))
            }
            is CreateTaskParams.Group -> if (params.departmentIds.isEmpty()) {
                return Result.failure(IllegalArgumentException("Выберите хотя бы одно направление"))
            }
        }
        return repository.createTask(params)
    }
}
