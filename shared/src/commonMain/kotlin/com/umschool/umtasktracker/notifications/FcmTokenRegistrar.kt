package com.umschool.umtasktracker.notifications

import com.umschool.umtasktracker.domain.repository.AuthRepository

expect suspend fun getFcmToken(): String?
expect suspend fun deleteLocalFcmToken()

class FcmTokenRegistrar(
    private val authRepository: AuthRepository
) {

    suspend fun registerAfterLogin() {
        val token = getFcmToken() ?: return
        authRepository.registerFcmToken(token)
    }

    suspend fun registerIfLoggedIn(token: String) {
        authRepository.registerFcmToken(token)
    }

    suspend fun unregisterOnLogout() {
        authRepository.deleteFcmToken()
        deleteLocalFcmToken()
    }
}
