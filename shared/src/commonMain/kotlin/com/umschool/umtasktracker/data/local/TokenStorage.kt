package com.umschool.umtasktracker.data.local

import kotlinx.coroutines.flow.Flow

// Платформозависимое хранилище токенов
expect class TokenStorage {
    suspend fun saveTokens(access: String, refresh: String)
    fun getAccessToken(): Flow<String?>
    suspend fun clearTokens()
}
