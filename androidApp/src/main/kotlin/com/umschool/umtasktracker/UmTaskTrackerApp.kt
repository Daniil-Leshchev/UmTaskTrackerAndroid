package com.umschool.umtasktracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.umschool.umtasktracker.di.appModule
import com.umschool.umtasktracker.notifications.UmFirebaseMessagingService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class UmTaskTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createTasksNotificationChannel()
        startKoin {
            androidContext(this@UmTaskTrackerApp)
            modules(appModule)
        }
    }

    private fun createTasksNotificationChannel() {
        val channel = NotificationChannel(
            UmFirebaseMessagingService.CHANNEL_TASKS,
            "Задачи",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления о новых задачах"
            enableVibration(true)
        }
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}
