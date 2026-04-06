package com.umschool.umtasktracker.di

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
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun commonModule(baseUrl: String, isDebug: Boolean) = module {
    single { createPlatformHttpClient(isDebug) }

    single { AuthApiService(get(), baseUrl) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LoadCatalogsUseCase(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
}
