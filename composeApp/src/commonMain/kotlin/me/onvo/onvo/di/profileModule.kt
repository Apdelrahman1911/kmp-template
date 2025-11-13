package me.onvo.onvo.di

import me.onvo.onvo.data.network.ProfileApiService
import me.onvo.onvo.data.network.ProfileApiServiceImpl
import me.onvo.onvo.data.repository.ProfileRepositoryImpl
import me.onvo.onvo.domain.repository.ProfileRepository
import me.onvo.onvo.domain.usecase.profile.GetCurrentUserStatusUseCase
import me.onvo.onvo.domain.usecase.profile.GetUserProfileUseCase
import me.onvo.onvo.presentation.viewmodel.ProfileViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {
    // API Service
    single<ProfileApiService> {
        ProfileApiServiceImpl(
            client = get(),
            baseUrl = get(named("baseUrl"))
        )
    }

    // Repository
    single<ProfileRepository> {
        ProfileRepositoryImpl(
            profileApiService = get(),
            preferencesManager = get()
        )
    }

    // Use Cases
    factory {
        GetCurrentUserStatusUseCase(
            profileRepository = get()
        )
    }

    factory {
        GetUserProfileUseCase(
            profileRepository = get()
        )
    }

    // ViewModel
    single {
        ProfileViewModel(
            getCurrentUserStatusUseCase = get(),
            getUserProfileUseCase = get()
        )
    }
}