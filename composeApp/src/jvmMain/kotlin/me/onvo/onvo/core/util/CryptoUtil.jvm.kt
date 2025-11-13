package me.onvo.onvo.core.util


import java.security.MessageDigest

actual object CryptoUtil {
    actual fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}