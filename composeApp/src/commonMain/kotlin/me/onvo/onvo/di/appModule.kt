package me.onvo.onvo.di

import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import me.onvo.onvo.theme.ThemeManager
import org.koin.dsl.module


val appModule = module {
    single { ThemeManager() }
    single { AuthViewModel() }
    single { SourcesViewModel(get()) }
}