package com.umschool.umtasktracker.di

import com.umschool.umtasktracker.BuildConfig

val appModule = createSharedModule(
    baseUrl = BuildConfig.BASE_URL,
    isDebug = BuildConfig.DEBUG
)
