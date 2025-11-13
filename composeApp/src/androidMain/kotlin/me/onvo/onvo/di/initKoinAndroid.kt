package me.onvo.onvo.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoinAndroid(app: Application) {
    startKoin {
        androidContext(app)
        modules(
            networkModule,
            repositoryModule,
            useCaseModule,
            appModule,
            dataStoreModule,
            viewModelModule,
            authModule,
            platformModule,
            profileModule,
        )
    }
}
