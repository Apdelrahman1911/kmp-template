package me.onvo.onvo.core.util

expect object CryptoUtil {
    fun md5(input: String): String
}