package me.onvo.onvo

import android.app.Application
import android.util.Log
import me.onvo.onvo.di.initKoin
import me.onvo.onvo.di.initKoinAndroid
import me.onvo.onvo.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OnvoApp : Application() {
    override fun onCreate() {
        super.onCreate()


        initKoinAndroid(this)
    }
}