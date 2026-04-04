package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.remote.api.ApiException

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
    runCatching { block() }.recoverCatching { throwable ->
        when (throwable) {
            is ApiException -> throw throwable
            else -> throw ApiException.NetworkError(throwable)
        }
    }
