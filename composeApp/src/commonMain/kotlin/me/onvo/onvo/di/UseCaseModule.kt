package me.onvo.onvo.di

import me.onvo.onvo.domain.usecase.GetSourcesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        GetSourcesUseCase(repository = get())
    }
}