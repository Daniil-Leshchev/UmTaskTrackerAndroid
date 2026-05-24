package com.umschool.umtasktracker.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.security.KeyStore

actual class TokenStorage(private val context: Context) {

    private val prefs: SharedPreferences = openEncryptedPrefsResilient(context)

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
        const val TAG = "TokenStorage"
        const val PREFS_FILE = "encrypted_auth_tokens"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val MASTER_KEY_ALIAS = MasterKey.DEFAULT_MASTER_KEY_ALIAS

        fun openEncryptedPrefsResilient(context: Context): SharedPreferences {
            return try {
                createEncryptedPrefs(context)
            } catch (e: Exception) {
                Log.w(TAG, "EncryptedSharedPreferences corrupted, resetting: ${e.message}")
                resetEncryptedStorage(context)
                createEncryptedPrefs(context)
            }
        }

        fun createEncryptedPrefs(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context, MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        fun resetEncryptedStorage(context: Context) {
            runCatching { context.deleteSharedPreferences(PREFS_FILE) }
            runCatching {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                if (keyStore.containsAlias(MASTER_KEY_ALIAS)) {
                    keyStore.deleteEntry(MASTER_KEY_ALIAS)
                }
            }
        }
    }
}
