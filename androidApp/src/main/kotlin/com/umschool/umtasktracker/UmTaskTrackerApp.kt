package com.umschool.umtasktracker

import android.app.Application
import com.umschool.umtasktracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class UmTaskTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@UmTaskTrackerApp)
            modules(appModule)
        }
    }
}
