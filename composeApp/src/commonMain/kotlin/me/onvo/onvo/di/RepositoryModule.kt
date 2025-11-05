package me.onvo.onvo.di

import me.onvo.onvo.data.repository.ItemsRepositoryImpl
import me.onvo.onvo.domain.repository.ItemsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ItemsRepository> {
        ItemsRepositoryImpl(api = get())
    }
}