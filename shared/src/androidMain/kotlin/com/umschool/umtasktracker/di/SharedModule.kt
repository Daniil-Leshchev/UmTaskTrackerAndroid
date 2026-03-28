package com.umschool.umtasktracker.di

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.AuthApiService
import com.umschool.umtasktracker.data.repository.AuthRepositoryImpl
import com.umschool.umtasktracker.data.repository.CatalogRepositoryImpl
import com.umschool.umtasktracker.domain.repository.AuthRepository
import com.umschool.umtasktracker.domain.repository.CatalogRepository
import com.umschool.umtasktracker.domain.usecase.LoadCatalogsUseCase
import com.umschool.umtasktracker.domain.usecase.LoginUseCase
import com.umschool.umtasktracker.domain.usecase.RegisterUseCase
import com.umschool.umtasktracker.presentation.auth.LoginViewModel
import com.umschool.umtasktracker.presentation.auth.RegisterViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Создаёт Koin-модуль с зависимостями shared-слоя
// baseUrl и isDebug передаются из androidApp
fun createSharedModule(baseUrl: String, isDebug: Boolean) = module {
    // Сеть
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 15_000
            }
            install(Logging) {
                level = if (isDebug) LogLevel.HEADERS else LogLevel.NONE
            }
        }
    }

    single { AuthApiService(get(), baseUrl) }

    // Хранилище
    single { TokenStorage(androidContext()) }

    // Data
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }

    // Domain
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LoadCatalogsUseCase(get()) }

    // Presentation
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
}
