package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.remote.api.ApiException
import com.umschool.umtasktracker.data.remote.api.AuthApiService
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

    private suspend fun <T> wrapApi(block: suspend () -> T): Result<T> =
        runCatching { block() }.recoverCatching { throwable ->
            when (throwable) {
                is ApiException -> throw throwable
                else -> throw ApiException.NetworkError(throwable)
            }
        }
}
