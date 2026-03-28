package com.umschool.umtasktracker.di

import com.umschool.umtasktracker.BuildConfig

// Модуль приложения — делегирует в shared-модуль, передавая конфигурацию
val appModule = createSharedModule(
    baseUrl = BuildConfig.BASE_URL,
    isDebug = BuildConfig.DEBUG
)
