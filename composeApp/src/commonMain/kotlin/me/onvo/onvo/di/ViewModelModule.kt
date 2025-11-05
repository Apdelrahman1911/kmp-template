package me.onvo.onvo.di

import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        SourcesViewModel(getSourcesUseCase = get())
    }
}