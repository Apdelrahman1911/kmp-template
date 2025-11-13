package me.onvo.onvo.core.util

import kotlinx.cinterop.*
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual object CryptoUtil {

    actual fun md5(input: String): String {
        // Convert input String → NSData
        val nsData = input.toNSData() ?: return ""

        // Allocate buffer for MD5 result
        val digest = UByteArray(CC_MD5_DIGEST_LENGTH)

        // Compute MD5
        nsData.bytes?.let { bytes ->
            digest.usePinned { pinned ->
                CC_MD5(bytes, nsData.length.toUInt(), pinned.addressOf(0))
            }
        } ?: return ""

        // Convert digest bytes to lowercase hex string
        return buildString(CC_MD5_DIGEST_LENGTH) {
            digest.forEach { append(it.toString(16).padStart(2, '0')) }
        }
    }
}

// ✅ Helper extension for UTF-8 conversion
private fun String.toNSData(): NSData? =
    (this as NSString).dataUsingEncoding(NSUTF8StringEncoding)
