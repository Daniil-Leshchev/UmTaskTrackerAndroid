package com.umschool.umtasktracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.content.edit

actual class TokenStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_auth_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _accessTokenFlow = MutableStateFlow(prefs.getString(KEY_ACCESS, null))

    actual suspend fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
        }
        _accessTokenFlow.value = access
    }

    actual fun getAccessToken(): Flow<String?> = _accessTokenFlow

    actual suspend fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS)
                .remove(KEY_REFRESH)
        }
        _accessTokenFlow.value = null
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
