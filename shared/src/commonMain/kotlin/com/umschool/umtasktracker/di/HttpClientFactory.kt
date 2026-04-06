package com.umschool.umtasktracker.di

import io.ktor.client.HttpClient

expect fun createPlatformHttpClient(isDebug: Boolean): HttpClient
