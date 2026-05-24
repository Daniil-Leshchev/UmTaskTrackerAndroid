package com.umschool.umtasktracker.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umschool.umtasktracker.MainActivity
import com.umschool.umtasktracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.mp.KoinPlatform

class UmFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        val registrar = KoinPlatform.getKoin().get<FcmTokenRegistrar>()
        serviceScope.launch {
            runCatching { registrar.registerIfLoggedIn(token) }
                .onFailure { Log.w(TAG, "FCM register failed: ${it.message}") }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Новая задача"
        val taskName = message.notification?.body
            ?: message.data["body"]
            ?: ""
        val taskId = message.data["task_id"]
        val deadlineIso = message.data["deadline"]

        val deadlineLine = deadlineIso?.let(::formatDeadlineLine)
        val body = if (deadlineLine != null) "$taskName\n$deadlineLine" else taskName

        showNotification(title, body, taskId)
    }

    private fun showNotification(title: String, body: String, taskId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            taskId?.let { putExtra(EXTRA_TASK_ID, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            taskId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_TASKS)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFFF26522.toInt())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = NotificationManagerCompat.from(this)
        try {
            manager.notify(taskId?.hashCode() ?: 0, notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "POST_NOTIFICATIONS permission missing, skipping notify: ${e.message}")
        }
    }

    private fun formatDeadlineLine(iso: String): String? = runCatching {
        val moscowDt: LocalDateTime = Instant.parse(iso).toLocalDateTime(TimeZone.of("Europe/Moscow"))
        val formatted = moscowDt.format(DEADLINE_FORMAT)
        "Выполнить до $formatted"
    }.getOrNull()

    companion object {
        private const val TAG = "UmFirebaseMsg"
        const val CHANNEL_TASKS = "tasks"
        const val EXTRA_TASK_ID = "task_id"

        private val DEADLINE_FORMAT = LocalDateTime.Format {
            dayOfMonth(Padding.ZERO); char('.')
            monthNumber(Padding.ZERO); char('.')
            year()
            chars(", ")
            hour(Padding.ZERO); char(':')
            minute(Padding.ZERO)
        }
    }
}
