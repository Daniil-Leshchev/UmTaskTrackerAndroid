package com.umschool.umtasktracker.di

import com.umschool.umtasktracker.data.remote.api.AuthApiService
import com.umschool.umtasktracker.data.remote.api.CuratorApiService
import com.umschool.umtasktracker.data.remote.api.ManagerApiService
import com.umschool.umtasktracker.data.repository.AuthRepositoryImpl
import com.umschool.umtasktracker.data.repository.CatalogRepositoryImpl
import com.umschool.umtasktracker.data.repository.CuratorRepositoryImpl
import com.umschool.umtasktracker.data.repository.ManagerRepositoryImpl
import com.umschool.umtasktracker.domain.repository.AuthRepository
import com.umschool.umtasktracker.domain.repository.CatalogRepository
import com.umschool.umtasktracker.domain.repository.CuratorRepository
import com.umschool.umtasktracker.domain.repository.ManagerRepository
import com.umschool.umtasktracker.domain.usecase.GetCuratorTasksUseCase
import com.umschool.umtasktracker.domain.usecase.GetManagerTasksUseCase
import com.umschool.umtasktracker.domain.usecase.LoadCatalogsUseCase
import com.umschool.umtasktracker.domain.usecase.LoginUseCase
import com.umschool.umtasktracker.domain.usecase.RegisterUseCase
import com.umschool.umtasktracker.presentation.auth.LoginViewModel
import com.umschool.umtasktracker.presentation.auth.RegisterViewModel
import com.umschool.umtasktracker.presentation.curator.CuratorTasksViewModel
import com.umschool.umtasktracker.presentation.manager.ManagerTasksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun commonModule(baseUrl: String, isDebug: Boolean) = module {
    single { createPlatformHttpClient(isDebug) }

    single { AuthApiService(get(), baseUrl) }
    single { CuratorApiService(get(), baseUrl) }
    single { ManagerApiService(get(), baseUrl) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }
    single<CuratorRepository> { CuratorRepositoryImpl(get(), get()) }
    single<ManagerRepository> { ManagerRepositoryImpl(get(), get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LoadCatalogsUseCase(get()) }
    factory { GetCuratorTasksUseCase(get()) }
    factory { GetManagerTasksUseCase(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { CuratorTasksViewModel(get()) }
    viewModel { ManagerTasksViewModel(get()) }
}
