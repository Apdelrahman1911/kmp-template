package me.onvo.onvo.di

import me.onvo.onvo.core.device.DeviceInfoProvider
import org.koin.dsl.module


actual val platformModule = module {
    single { DeviceInfoProvider() }
}
