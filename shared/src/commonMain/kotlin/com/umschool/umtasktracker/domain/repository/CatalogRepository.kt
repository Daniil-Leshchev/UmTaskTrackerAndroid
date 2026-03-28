package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.CatalogItem

interface CatalogRepository {
    suspend fun getRoles(): Result<List<CatalogItem>>
    suspend fun getSubjects(): Result<List<CatalogItem>>
    suspend fun getDepartments(): Result<List<CatalogItem>>
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ): Result<Unit>
}
