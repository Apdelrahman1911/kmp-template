package me.onvo.onvo.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import me.onvo.onvo.domain.model.DeviceInfo
import java.util.*

actual class DeviceInfoProvider(private val context: Context) {

    @SuppressLint("HardwareIds")
    actual fun getDeviceInfo(): DeviceInfo {
        val deviceId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }

        return DeviceInfo(
            deviceId = deviceId ?: "Unknown",
            brand = Build.BRAND ?: "Unknown",
            model = Build.MODEL ?: "Unknown",
            systemName = "Android",
            systemVersion = Build.VERSION.RELEASE ?: "Unknown",
            appVersion = getAppVersion(),
            buildNumber = getAppBuildNumber(),
            uniqueId = deviceId ?: "Unknown",
            deviceName = Build.DEVICE ?: "Unknown",
            isTablet = isTablet(),
            carrier = getCarrier(),
            timezone = TimeZone.getDefault().id ?: "Unknown"
        )
    }

    private fun getAppVersion(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    private fun getAppBuildNumber(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                pInfo.longVersionCode.toString()
            else
                @Suppress("DEPRECATION") pInfo.versionCode.toString()
        } catch (e: Exception) {
            "1"
        }
    }

    private fun isTablet(): Boolean {
        val configuration = context.resources.configuration
        return (configuration.screenLayout and
                android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK) >=
                android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun getCarrier(): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.networkOperatorName.takeIf { it.isNotEmpty() } ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
