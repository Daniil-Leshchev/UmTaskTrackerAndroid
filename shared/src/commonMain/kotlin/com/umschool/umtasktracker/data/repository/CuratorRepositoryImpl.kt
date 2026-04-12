package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.remote.api.CuratorApiService
import com.umschool.umtasktracker.data.remote.dto.TaskDto

class CuratorRepositoryImpl(
    private val api: CuratorApiService
) {
    suspend fun getTasks(): List<TaskDto> {
        return api.getTasks()
    }
}