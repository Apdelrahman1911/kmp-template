package me.onvo.onvo.di

import me.onvo.onvo.data.repository.ItemsRepositoryImpl
import me.onvo.onvo.domain.repository.ItemsRepository
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import me.onvo.onvo.theme.ThemeViewModel
import org.koin.dsl.module

val viewModelModule = module {
//    single< SourcesViewModel>  {
//        SourcesViewModel(getSourcesUseCase = get())
//    }
    single<ThemeViewModel> {
        ThemeViewModel(get())
    }

}