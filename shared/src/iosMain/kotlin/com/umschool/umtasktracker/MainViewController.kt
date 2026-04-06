package com.umschool.umtasktracker

import androidx.compose.ui.window.ComposeUIViewController
import com.umschool.umtasktracker.di.createSharedModule
import com.umschool.umtasktracker.ui.navigation.AppNavGraph
import com.umschool.umtasktracker.ui.theme.UmTaskTrackerTheme
import org.koin.core.context.startKoin

fun initKoin(baseUrl: String, isDebug: Boolean) {
    startKoin {
        modules(createSharedModule(baseUrl, isDebug))
    }
}

fun MainViewController() = ComposeUIViewController {
    UmTaskTrackerTheme {
        AppNavGraph()
    }
}
