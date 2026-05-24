package com.umschool.umtasktracker.notifications

actual suspend fun getFcmToken(): String? = null

actual suspend fun deleteLocalFcmToken() {
}
