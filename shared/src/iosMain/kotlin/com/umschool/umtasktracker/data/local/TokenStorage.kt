package com.umschool.umtasktracker.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class TokenStorage {

    actual suspend fun saveTokens(access: String, refresh: String) {

    }

    actual fun getAccessToken(): Flow<String?> = flowOf(null)

    actual suspend fun clearTokens() {

    }
}
