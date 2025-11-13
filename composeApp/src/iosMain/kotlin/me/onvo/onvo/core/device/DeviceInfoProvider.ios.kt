package me.onvo.onvo.core.device

import me.onvo.onvo.domain.model.DeviceInfo


import platform.UIKit.*
import platform.Foundation.*

actual class DeviceInfoProvider {

    actual fun getDeviceInfo(): DeviceInfo {
        val device = UIDevice.currentDevice

        return DeviceInfo(
            deviceId = device.identifierForVendor?.UUIDString ?: "",
            brand = "Apple",
            model = device.model?: "Unknown",
            systemName = device.systemName ?: "iOS",
            systemVersion = device.systemVersion?: "Unknown",
            appVersion = getAppVersion(),
            buildNumber = getBuildNumber(),
            uniqueId = device.identifierForVendor?.UUIDString ?: "",
            deviceName = device.name ?: "Unknown",
            isTablet = device.userInterfaceIdiom == UIUserInterfaceIdiomPad,
            carrier = getCarrier(),
            timezone = NSTimeZone.localTimeZone.name
        )
    }

    private fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary
            ?.get("CFBundleShortVersionString") as? String ?: "1.0.0"
    }

    private fun getBuildNumber(): String {
        return NSBundle.mainBundle.infoDictionary
            ?.get("CFBundleVersion") as? String ?: "1"
    }

    private fun getCarrier(): String? {
        // iOS carrier info requires CoreTelephony framework
        return null
    }
}