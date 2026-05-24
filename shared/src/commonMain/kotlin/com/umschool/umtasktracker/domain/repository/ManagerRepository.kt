package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.domain.model.TaskDetail

interface ManagerRepository {

    suspend fun getTasks(): Result<List<ManagerTask>>

    suspend fun getTaskDetails(
        taskId: String
    ): Result<List<TaskDetail>>

    suspend fun getAssignmentPolicy(): Result<AssignmentPolicy>

    suspend fun getRecipients(): Result<List<Recipient>>

    suspend fun createTask(
        params: CreateTaskParams
    ): Result<CreateTaskResult>
}