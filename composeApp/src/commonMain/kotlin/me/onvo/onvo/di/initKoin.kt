package me.onvo.onvo.di


import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            networkModule,
            repositoryModule,
            useCaseModule,
            appModule,
//            viewModelModule
        )
    }
}