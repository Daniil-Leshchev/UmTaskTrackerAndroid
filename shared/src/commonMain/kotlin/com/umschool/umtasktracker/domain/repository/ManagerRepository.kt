package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.ManagerTask

interface ManagerRepository {
    suspend fun getTasks(): Result<List<ManagerTask>>
}