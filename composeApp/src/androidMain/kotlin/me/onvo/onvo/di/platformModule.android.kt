package me.onvo.onvo.di

import org.koin.core.module.Module

import me.onvo.onvo.core.device.DeviceInfoProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { DeviceInfoProvider(androidContext()) }
}
