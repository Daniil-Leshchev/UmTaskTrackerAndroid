package com.umschool.umtasktracker.notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual suspend fun getFcmToken(): String? = try {
    FirebaseMessaging.getInstance().token.await()
} catch (e: Exception) {
    null
}

actual suspend fun deleteLocalFcmToken() {
    try {
        FirebaseMessaging.getInstance().deleteToken().await()
    } catch (_: Exception) {
    }
}
