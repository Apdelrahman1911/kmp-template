package me.onvo.onvo.core.device

import me.onvo.onvo.domain.model.DeviceInfo

expect class DeviceInfoProvider {
    fun getDeviceInfo(): DeviceInfo
}
