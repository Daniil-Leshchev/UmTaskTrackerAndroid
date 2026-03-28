package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.remote.api.ApiException
import com.umschool.umtasktracker.data.remote.api.AuthApiService
import com.umschool.umtasktracker.data.remote.dto.RegisterRequest
import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val apiService: AuthApiService
) : CatalogRepository {

    override suspend fun getRoles(): Result<List<CatalogItem>> = wrapApi {
        apiService.getRoles().map { CatalogItem(id = it.id, name = it.name) }
    }

    override suspend fun getSubjects(): Result<List<CatalogItem>> = wrapApi {
        apiService.getSubjects().map { CatalogItem(id = it.id, name = it.name) }
    }

    override suspend fun getDepartments(): Result<List<CatalogItem>> = wrapApi {
        apiService.getDepartments().map { CatalogItem(id = it.id, name = it.name) }
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ): Result<Unit> = wrapApi {
        apiService.register(
            RegisterRequest(
                email = email,
                password = password,
                name = "$firstName $lastName".trim(),
                firstName = firstName,
                lastName = lastName,
                roleId = roleId,
                subjectId = subjectId,
                departmentId = departmentId
            )
        )
    }

    private suspend fun <T> wrapApi(block: suspend () -> T): Result<T> =
        runCatching { block() }.recoverCatching { throwable ->
            println("CATALOG/REGISTER ERROR: ${throwable::class.simpleName}: ${throwable.message}")
            when (throwable) {
                is ApiException -> throw throwable
                else -> throw ApiException.NetworkError(throwable)
            }
        }
}
