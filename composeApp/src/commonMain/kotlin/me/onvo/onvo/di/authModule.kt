package me.onvo.onvo.di

import me.onvo.onvo.data.network.AuthApiService
import me.onvo.onvo.data.network.AuthApiServiceImpl
import me.onvo.onvo.data.network.PasswordResetApiService
import me.onvo.onvo.data.network.PasswordResetApiServiceImpl
import me.onvo.onvo.data.repository.AuthRepositoryImpl
import me.onvo.onvo.data.repository.PasswordResetRepositoryImpl
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.domain.repository.PasswordResetRepository
import me.onvo.onvo.domain.usecase.auth.*
import me.onvo.onvo.domain.usecase.passwordreset.*
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.PasswordResetViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    // Auth API Service
    single<AuthApiService> {
        AuthApiServiceImpl(
            client = get(),
            baseUrl = get(named("baseUrl"))
        )
    }

    // Password Reset API Service
    single<PasswordResetApiService> {
        PasswordResetApiServiceImpl(
            client = get(),
            baseUrl = get(named("baseUrl"))
        )
    }

    // Auth Repository
    single<AuthRepository> {
        AuthRepositoryImpl(
            authApiService = get(),
            preferencesManager = get()
        )
    }

    // Password Reset Repository
    single<PasswordResetRepository> {
        PasswordResetRepositoryImpl(
            passwordResetApiService = get()
        )
    }

    // Auth Use Cases
    factory {
        GetTokenUseCase(
            authRepository = get(),
            deviceInfoProvider = get()
        )
    }

    factory {
        CheckInputUseCase(
            authRepository = get()
        )
    }

    factory {
        LoginUseCase(
            authRepository = get()
        )
    }

    factory {
        LogoutUseCase(
            authRepository = get()
        )
    }

    factory {
        GetUserSessionUseCase(
            authRepository = get()
        )
    }

    // Password Reset Use Cases
    factory {
        RequestResetCodeUseCase(
            passwordResetRepository = get(),
            authRepository = get()
        )
    }

    factory {
        SubmitResetCodeUseCase(
            passwordResetRepository = get(),
            authRepository = get()
        )
    }

    factory {
        ChangePasswordUseCase(
            passwordResetRepository = get(),
            authRepository = get()
        )
    }

    // ViewModels
    single {
        AuthViewModel(
            getTokenUseCase = get(),
            checkInputUseCase = get(),
            loginUseCase = get(),
            logoutUseCase = get(),
            getUserSessionUseCase = get(),
            getCurrentUserStatusUseCase = get()  // Added this dependency
        )
    }

    single {
        PasswordResetViewModel(
            checkInputUseCase = get(),
            requestResetCodeUseCase = get(),
            submitResetCodeUseCase = get(),
            changePasswordUseCase = get(),
            authRepository = get()
        )
    }
}