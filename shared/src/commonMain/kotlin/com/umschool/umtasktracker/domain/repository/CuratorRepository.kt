package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.CuratorTaskReport
import com.umschool.umtasktracker.domain.model.SelectedFile

interface CuratorRepository {
    suspend fun getTasks(): Result<List<CuratorTask>>

    suspend fun getReport(taskId: String, email: String): Result<CuratorTaskReport>

    suspend fun submitReport(
        taskId: String,
        reportText: String,
        files: List<SelectedFile>,
    ): Result<Unit>
}
