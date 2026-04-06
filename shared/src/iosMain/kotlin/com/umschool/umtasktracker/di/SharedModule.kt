package com.umschool.umtasktracker.di

import com.umschool.umtasktracker.data.local.TokenStorage
import org.koin.dsl.module

fun createSharedModule(baseUrl: String, isDebug: Boolean) = module {
    includes(commonModule(baseUrl, isDebug))
    single { TokenStorage() }
}
