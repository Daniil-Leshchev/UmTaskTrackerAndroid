package com.umschool.umtasktracker.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSUserDefaults


actual class TokenStorage {

    private val defaults: NSUserDefaults =
        NSUserDefaults(suiteName = SUITE_NAME) ?: NSUserDefaults.standardUserDefaults

    private val _accessTokenFlow = MutableStateFlow(defaults.stringForKey(KEY_ACCESS))

    actual suspend fun saveTokens(access: String, refresh: String) {
        defaults.setObject(access, forKey = KEY_ACCESS)
        defaults.setObject(refresh, forKey = KEY_REFRESH)
        _accessTokenFlow.value = access
    }

    actual fun getAccessToken(): Flow<String?> = _accessTokenFlow

    actual suspend fun clearTokens() {
        defaults.removeObjectForKey(KEY_ACCESS)
        defaults.removeObjectForKey(KEY_REFRESH)
        _accessTokenFlow.value = null
    }

    private companion object {
        const val SUITE_NAME = "com.umschool.umtasktracker.auth"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
