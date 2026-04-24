package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.CuratorTask

interface CuratorRepository {
    suspend fun getTasks(): Result<List<CuratorTask>>
}
