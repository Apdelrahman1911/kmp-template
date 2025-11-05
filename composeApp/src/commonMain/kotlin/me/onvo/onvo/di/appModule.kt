package me.onvo.onvo.di

import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import org.koin.dsl.module


val appModule = module {
    single { AuthViewModel() }
    single { SourcesViewModel(get()) }
    // Add other ViewModels here
}