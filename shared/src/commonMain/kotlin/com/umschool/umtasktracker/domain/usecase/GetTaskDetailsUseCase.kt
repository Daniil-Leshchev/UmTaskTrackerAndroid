package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.TaskDetail
import com.umschool.umtasktracker.domain.repository.ManagerRepository

class GetTaskDetailsUseCase(
    private val repository: ManagerRepository
) {

    suspend operator fun invoke(
        taskId: String
    ): Result<List<TaskDetail>> {

        return repository.getTaskDetails(taskId)
    }
}