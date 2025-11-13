package me.onvo.onvo.core.device

import me.onvo.onvo.domain.model.DeviceInfo

import java.net.InetAddress
import java.util.*

actual class DeviceInfoProvider {
    actual fun getDeviceInfo(): DeviceInfo {
        val osName = System.getProperty("os.name") ?: "Unknown"
        val osVersion = System.getProperty("os.version") ?: "Unknown"
        val hostName = try {
            InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            "Desktop"
        }

        return DeviceInfo(
            deviceId = hostName,
            brand = "Desktop",
            model = osName,
            systemName = osName,
            systemVersion = osVersion,
            appVersion = "1.0.0",
            buildNumber = "1",
            uniqueId = hostName,
            deviceName = hostName,
            isTablet = false,
            carrier = null,
            timezone = TimeZone.getDefault().id
        )
    }
}