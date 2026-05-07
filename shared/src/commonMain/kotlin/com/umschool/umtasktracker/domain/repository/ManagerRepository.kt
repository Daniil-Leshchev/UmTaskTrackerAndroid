package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.model.Recipient

interface ManagerRepository {
    suspend fun getTasks(): Result<List<ManagerTask>>

    suspend fun getAssignmentPolicy(): Result<AssignmentPolicy>

    suspend fun getRecipients(): Result<List<Recipient>>

    suspend fun createTask(params: CreateTaskParams): Result<CreateTaskResult>
}
